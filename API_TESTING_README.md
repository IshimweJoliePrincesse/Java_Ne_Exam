# Utility Billing API Testing Guide

Use this guide in Swagger UI to test every endpoint and every role.

Swagger URL:

```text
http://localhost:8080/swagger-ui.html
```

Seeded admin:

```text
email: jolieprincesseishimwe@gmail.com
password: Jolie@123
```

After every login, copy the JWT token, click **Authorize** in Swagger, and paste:

```text
Bearer YOUR_TOKEN_HERE
```

## Role Rules To Verify

- All self-registered users start as `ROLE_CUSTOMER`.
- A registered customer is inactive until OTP verification.
- Admin can view users, get user by ID, delete users, upgrade roles, and revoke roles.
- Admin can activate or deactivate customer profiles.
- Operator captures meter readings.
- Finance generates/approves bills and records payments.
- Customer can view and update only their own account/profile, bills, payments, notifications, and PDF bill.

## 1. Authentication APIs

### 1.1 Login As Admin

`POST /api/auth/login`

```json
{
  "email": "jolieprincesseishimwe@gmail.com",
  "password": "Jolie@123"
}
```

Expected:

- `200 OK`
- JWT token returned
- Use token as admin token

### 1.2 Register Customer

`POST /api/auth/register`

```json
{
  "fullName": "Test Customer One",
  "email": "customer1@example.com",
  "phoneNumber": "+250788123456",
  "nationalId": "1198000000000003",
  "address": "Kigali, Nyarugenge",
  "password": "Customer@123"
}
```

Expected:

- Customer user is created as `ROLE_CUSTOMER`
- User status is `INACTIVE`
- Customer profile status is `INACTIVE`
- OTP is sent to `customer1@example.com`

### 1.3 Verify OTP

`POST /api/auth/verify-otp`

```json
{
  "email": "customer1@example.com",
  "otp": "OTP_FROM_EMAIL"
}
```

Expected:

- User becomes `ACTIVE`
- Customer profile becomes `ACTIVE`
- `emailVerified = true`

### 1.4 Login As Customer

`POST /api/auth/login`

```json
{
  "email": "customer1@example.com",
  "password": "Customer@123"
}
```

Expected:

- `200 OK`
- JWT token returned
- Save as customer token

### 1.5 Forgot Password

`POST /api/auth/forgot-password`

```json
{
  "email": "customer1@example.com"
}
```

Expected:

- Password reset OTP is sent by email
- OTP is not returned in Swagger response

### 1.6 Reset Password

`POST /api/auth/reset-password`

```json
{
  "email": "customer1@example.com",
  "otp": "OTP_FROM_EMAIL",
  "newPassword": "NewCustomer@123"
}
```

Expected:

- Password changes successfully
- Login with old password fails
- Login with new password succeeds

## 2. User Management APIs

Use admin token unless testing negative permissions.

### 2.1 List Users

`GET /api/users`

Expected:

- Admin sees all users
- Customer gets `403 Forbidden`
- Operator gets `403 Forbidden`
- Finance gets `403 Forbidden`

### 2.2 Get User By ID

`GET /api/users/{id}`

Expected:

- Admin can fetch any user by integer ID
- Non-admin roles get `403 Forbidden`

### 2.3 Get My Account

`GET /api/users/me`

Expected:

- Admin can see own account
- Customer can see own account
- Operator can see own account
- Finance can see own account

### 2.4 Upgrade User Role

Use admin token.

`PATCH /api/users/{id}/role`

Upgrade customer to operator:

```json
{
  "role": "ROLE_OPERATOR"
}
```

Upgrade another customer to finance:

```json
{
  "role": "ROLE_FINANCE"
}
```

Expected:

- Role changes
- User receives email notification
- User can log in and access new role endpoints

### 2.5 Revoke User Role

Use admin token.

`PATCH /api/users/{id}/role/revoke`

Expected:

- User role becomes `ROLE_CUSTOMER`
- User receives email notification
- User loses operator/finance permissions

### 2.6 Delete User

Use admin token.

`DELETE /api/users/{id}`

Expected:

- `204 No Content`
- Deleted user can no longer log in

## 3. Customer APIs

### 3.1 Get My Customer Profile

Use customer token.

`GET /api/customers/me`

Expected:

- Customer sees only their own customer profile

### 3.2 Update My Customer Profile

Use customer token.

`PUT /api/customers/me`

```json
{
  "fullName": "Test Customer Updated",
  "phoneNumber": "+250788654321",
  "address": "Kigali, Gasabo"
}
```

Expected:

- Customer profile updates
- Matching user full name and phone number also update

### 3.3 Create Customer Manually

Use admin or operator token.

`POST /api/customers`

```json
{
  "fullName": "Manual Customer",
  "nationalId": "1198000000000004",
  "email": "manual.customer@example.com",
  "phoneNumber": "+250788222333",
  "address": "Kigali, Kicukiro"
}
```

Expected:

- Customer created
- Duplicate National ID returns `409 Conflict`

### 3.4 List Customers

Use admin or operator token.

`GET /api/customers`

Expected:

- Admin can list customers
- Operator can list customers
- Finance should get `403 Forbidden`
- Customer should get `403 Forbidden`

### 3.5 Get Customer By ID

Use admin or operator token.

`GET /api/customers/{id}`

Expected:

- Admin/operator can fetch customer by UUID
- Customer should use `/api/customers/me` instead

### 3.6 Update Customer By ID

Use admin or operator token.

`PUT /api/customers/{id}`

```json
{
  "fullName": "Manual Customer Updated",
  "nationalId": "1198000000000004",
  "email": "manual.customer@example.com",
  "phoneNumber": "+250788222333",
  "address": "Kigali, Remera"
}
```

Expected:

- Admin/operator can update customer profile

### 3.7 Activate Or Deactivate Customer

Use admin token only.

`PATCH /api/customers/{id}/status`

Deactivate:

```json
{
  "status": "INACTIVE"
}
```

Activate:

```json
{
  "status": "ACTIVE"
}
```

Expected:

- Admin can activate/deactivate customer
- Matching `AppUser.status` is synchronized
- When customer is deactivated, all meters owned by that customer become `INACTIVE`
- Reactivating the customer does not automatically reactivate meters; activate meters explicitly if needed
- Deactivated customer cannot log in
- Operator/finance/customer get `403 Forbidden`

## 4. Meter APIs

Use admin or operator token.

### 4.1 Create Meter

`POST /api/meters`

```json
{
  "meterNumber": "WTR-TEST-001",
  "meterType": "WATER",
  "installationDate": "2025-01-01",
  "customerId": "PASTE_CUSTOMER_ID"
}
```

Expected:

- Meter is linked to customer
- Duplicate meter number returns `409 Conflict`

### 4.2 List Meters

`GET /api/meters`

Expected:

- Admin/operator can list meters
- Finance/customer get `403 Forbidden`

### 4.3 List Meters By Customer

`GET /api/meters/customer/{customerId}`

Expected:

- Returns all meters assigned to that customer

### 4.4 Activate Or Deactivate Meter

`PATCH /api/meters/{id}/status`

```json
{
  "status": "ACTIVE"
}
```

Expected:

- Inactive meter cannot receive readings

## 5. Tariff APIs

Use admin token for create/update. Finance can view.

### 5.1 Create Flat Tariff

`POST /api/tariffs`

```json
{
  "name": "Water Standard 2026",
  "meterType": "WATER",
  "tariffType": "FLAT",
  "pricePerUnit": 500,
  "fixedCharge": 200,
  "vatPercent": 18,
  "latePenaltyPercent": 5,
  "effectiveDate": "2026-06-05",
  "tiers": []
}
```

### 5.2 Create Tiered Tariff

`POST /api/tariffs`

```json
{
  "name": "Electricity Tiered 2026",
  "meterType": "ELECTRICITY",
  "tariffType": "TIERED",
  "pricePerUnit": 0,
  "fixedCharge": 500,
  "vatPercent": 18,
  "latePenaltyPercent": 5,
  "effectiveDate": "2026-06-05",
  "tiers": [
    {
      "minUnits": 0,
      "maxUnits": 50,
      "pricePerUnit": 250
    },
    {
      "minUnits": 50,
      "maxUnits": 100,
      "pricePerUnit": 300
    },
    {
      "minUnits": 100,
      "maxUnits": null,
      "pricePerUnit": 350
    }
  ]
}
```

Expected:

- Admin can create tariffs
- Operator/customer get `403 Forbidden`

### 5.3 List All Tariffs

`GET /api/tariffs`

Expected:

- Admin and finance can view all versions

### 5.4 List Active Tariffs

`GET /api/tariffs/active`

Expected:

- Returns only active tariffs

### 5.5 Update Tariff

`PUT /api/tariffs/{id}`

Expected:

- Creates new tariff version
- Old tariff becomes inactive

## 6. Meter Reading APIs

Use operator token for capture. Admin/finance/operator can view single/by-meter readings depending on endpoint rules.

### 6.1 Capture Meter Reading

`POST /api/meter-readings`

```json
{
  "meterId": "PASTE_METER_ID",
  "previousReading": 100,
  "currentReading": 150,
  "readingDate": "2026-06-05"
}
```

Expected:

- Consumption becomes `50`
- Same meter/month/year again returns `409 Conflict`
- Current reading less than or equal to previous returns `400 Bad Request`
- Inactive meter returns `400 Bad Request`

### 6.2 List Meter Readings

`GET /api/meter-readings`

Expected:

- Admin/finance can list readings
- Operator should not list all readings if restricted

### 6.3 Get Reading By ID

`GET /api/meter-readings/{id}`

Expected:

- Admin/finance/operator can view

### 6.4 List Readings By Meter

`GET /api/meter-readings/meter/{meterId}`

Expected:

- Admin/finance/operator can view meter reading history

## 7. Bill APIs

Use finance or admin token.

### 7.1 Generate Bill

`POST /api/bills/generate/{meterReadingId}`

Expected:

- Bill generated from meter reading
- Bill status is `PENDING`
- Inactive customer cannot receive bill

Save:

- `billId`
- `billReference`
- `outstandingBalance`

### 7.2 Approve Bill

`POST /api/bills/{id}/approve`

Expected:

- Status becomes `APPROVED`
- Database trigger creates bill notification

### 7.3 List Bills

`GET /api/bills`

Expected:

- Admin/finance can list all bills
- Customer gets `403 Forbidden`

### 7.4 Get Bill By ID

`GET /api/bills/{id}`

Expected:

- Admin/finance can view details

### 7.5 Download Bill PDF

`GET /api/bills/{id}/pdf`

Expected:

- Admin/finance can download any bill PDF
- Customer can download only their own bill PDF

### 7.6 List Customer Bills

`GET /api/bills/customer/{customerId}`

Expected:

- Admin/finance can view customer bills
- Customer can view only their own bills

### 7.7 Get Bill By Reference

`GET /api/bills/reference/{reference}`

Expected:

- Admin/finance can find bill by reference

## 8. Payment APIs

Use finance or admin token.

### 8.1 Record Partial Payment

`POST /api/payments`

```json
{
  "billId": "PASTE_BILL_ID",
  "amountPaid": 1000,
  "paymentMethod": "MOBILE_MONEY",
  "paymentDate": "2026-06-05",
  "transactionReference": "MOMO-TEST-001"
}
```

Expected:

- Bill status becomes `PARTIALLY_PAID`
- Outstanding balance is reduced

### 8.2 Record Full Payment

Use remaining outstanding balance.

```json
{
  "billId": "PASTE_BILL_ID",
  "amountPaid": 28400,
  "paymentMethod": "MOBILE_MONEY",
  "paymentDate": "2026-06-05",
  "transactionReference": "MOMO-TEST-002"
}
```

Expected:

- Bill status becomes `PAID`
- Outstanding balance becomes `0`
- Database trigger creates payment confirmation notification

### 8.3 List Payments

`GET /api/payments`

Expected:

- Admin/finance can list all payments

### 8.4 List Payments By Bill

`GET /api/payments/bill/{billId}`

Expected:

- Admin/finance can view payments for bill

### 8.5 List Customer Payment History

`GET /api/payments/customer/{customerId}`

Expected:

- Admin/finance can view customer payments
- Customer can view only their own payment history

## 9. Notification APIs

### 9.1 List Customer Notifications

`GET /api/notifications/customer/{customerId}`

Expected:

- Admin/finance can view customer notifications
- Customer can view only their own notifications
- Should include bill-approved and full-payment notifications after those events

### 9.2 Mark Notification As Read

`PATCH /api/notifications/{id}/read`

Expected:

- `isRead = true`
- Customer can mark only their own notifications

## 10. Full Role Test Matrix

### Admin Must Be Able To

- Login
- List users
- Get user by ID
- Upgrade user role
- Revoke user role
- Delete user
- Activate/deactivate customer
- Manage tariffs
- Generate and approve bills
- Record payments
- View notifications

### Admin Must Not Need To

- Create users manually, except through customer registration and role upgrade
- Update user profile directly

### Customer Must Be Able To

- Register
- Verify OTP
- Login after activation
- Get own account with `/api/users/me`
- Get own profile with `/api/customers/me`
- Update own profile with `/api/customers/me`
- View own bills
- Download own bill PDF
- View own payment history
- View own notifications
- Mark own notifications as read

### Customer Must Not Be Able To

- List all users
- Get another user by ID
- Delete users
- Upgrade/revoke roles
- List all customers
- Activate/deactivate customers
- Create tariffs
- Capture meter readings
- Generate/approve bills
- Record payments
- View another customer's bills/payments/notifications

### Operator Must Be Able To

- View customers
- Manage meters
- Capture meter readings
- View meter reading by ID or by meter

### Operator Must Not Be Able To

- Manage user roles
- Manage tariffs
- Generate/approve bills
- Record payments

### Finance Must Be Able To

- View tariffs
- View meter readings
- Generate bills
- Approve bills
- Record payments
- View bills/payments/notifications

### Finance Must Not Be Able To

- Manage user roles
- Activate/deactivate customers
- Capture meter readings
- Create/update tariffs

## 11. Validation Tests

Invalid phone number:

```json
{
  "phoneNumber": "0788123456"
}
```

Expected:

- Validation error because phone must start with `+` and have 10 to 12 digits.

Invalid National ID:

```json
{
  "nationalId": "12345"
}
```

Expected:

- Validation error because National ID must be exactly 16 digits.

Weak password:

```json
{
  "password": "password"
}
```

Expected:

- Validation error because password must contain uppercase, lowercase, number, special character, and at least 8 characters.

Duplicate National ID:

- Register/create the same National ID twice.

Expected:

- `409 Conflict`

Duplicate meter number:

- Create the same meter number twice.

Expected:

- `409 Conflict`

Duplicate transaction reference:

- Record two payments with the same `transactionReference`.

Expected:

- `409 Conflict`

Overpayment:

- Pay more than outstanding balance.

Expected:

- `400 Bad Request`

Duplicate monthly reading:

- Capture two readings for the same meter and same month/year.

Expected:

- `409 Conflict`
