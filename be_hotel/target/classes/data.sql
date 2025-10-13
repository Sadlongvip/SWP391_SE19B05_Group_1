-- Insert roles data if not exists
INSERT INTO roles (id, role_name, role_description) VALUES (1, 'Customer', 'Regular customer who can book rooms, view services, and manage their own bookings') ON CONFLICT DO NOTHING;
INSERT INTO roles (id, role_name, role_description) VALUES (2, 'Employee', 'Hotel staff member who can assist customers, manage bookings, and access employee-level features') ON CONFLICT DO NOTHING;
INSERT INTO roles (id, role_name, role_description) VALUES (3, 'Admin', 'System administrator with full access to manage accounts, employees, rooms, and all hotel operations including editing or deactivating accounts and employees') ON CONFLICT DO NOTHING;
