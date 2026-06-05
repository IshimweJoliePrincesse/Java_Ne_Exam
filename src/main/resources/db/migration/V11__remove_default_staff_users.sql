-- Removes the earlier sample staff users so all non-admin users are customers unless an admin upgrades their role.
DELETE FROM app_users
WHERE email IN ('operator@utility.rw', 'finance@utility.rw')
  AND full_name IN ('Default Operator', 'Default Finance');
