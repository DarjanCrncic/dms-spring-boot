
-- Populate 'folder' Table with INSERT IGNORE
INSERT IGNORE INTO dms_folder (id, name) VALUES (1, '/');

-- Populate 'privilege' Table with INSERT IGNORE
INSERT IGNORE INTO dms_privilege (id, name) VALUES
    (1, 'READ_PRIVILEGE'),
    (2, 'WRITE_PRIVILEGE'),
    (3, 'CREATE_PRIVILEGE'),
    (4, 'VERSION_PRIVILEGE'),
    (5, 'ADMINISTRATION_PRIVILEGE'),
    (6, 'DELETE_PRIVILEGE');

-- Populate 'role' Table with INSERT IGNORE
INSERT IGNORE INTO dms_role (id, name) VALUES
    (1, 'ROLE_ADMIN'),
    (2, 'ROLE_USER');

-- Populate 'type' Table with INSERT IGNORE
INSERT IGNORE INTO dms_type (id, type_name) VALUES (1, 'document');