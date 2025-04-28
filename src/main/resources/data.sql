INSERT INTO integration_flows (id, name, schedule_cron, active)
VALUES (1, 'syncAppData', '0 0/5 * * * ?', true);

-- Step 1: Clear staging table
INSERT INTO integration_steps (id, flow_id, step_order, step_type, config) 
VALUES (1, 1, 1, 'sql', '{"query": "DELETE FROM staging_table"}');

-- Step 2: Source data query
INSERT INTO integration_steps (id, flow_id, step_order, step_type, config) 
VALUES (2, 1, 2, 'sql', '{"query": "SELECT id, name FROM app_source"}');

-- Step 3: Clean data processor
/*INSERT INTO integration_steps (id, flow_id, step_order, step_type, config) 
VALUES (3, 1, 3, 'processor', '{"bean": "cleanDataProcessor"}');*/

-- Add mapping processor after source query and before staging insert
INSERT INTO integration_steps (id, flow_id, step_order, step_type, config) 
VALUES (3, 1, 3, 'processor', '{"bean": "dynamicMappingProcessor"}');

-- Step 4: Insert into staging
INSERT INTO integration_steps (id, flow_id, step_order, step_type, config) 
VALUES (4, 1, 4, 'sql', '{"query": "INSERT INTO staging_table (id, name) VALUES (:#id, :#name)", "batch": true}');

-- Step 5: Upsert processor
INSERT INTO integration_steps (id, flow_id, step_order, step_type, config) 
VALUES (5, 1, 5, 'processor', '{"bean": "upsertProcessor"}');

-- Step 6: Console logging
INSERT INTO integration_steps (id, flow_id, step_order, step_type, config) 
VALUES (6, 1, 6, 'processor', '{"bean": "consoleLogProcessor"}');

-- Step 7: MongoDB insertion
INSERT INTO integration_steps (id, flow_id, step_order, step_type, config) 
VALUES (7, 1, 7, 'mongodb', '{"collection": "syncedData", "operation": "insert"}');

-- Step 8: Header logging
INSERT INTO integration_steps (id, flow_id, step_order, step_type, config) 
VALUES (8, 1, 8, 'processor', '{"bean": "headerLogProcessor"}');

-- Add some sample mappings
INSERT INTO column_mappings (flow_id, source_column, target_column, transform_expression) 
VALUES 
(1, 'id', 'id', null),
(1, 'name', 'name', 'TRIM');

-- Step 5: REST endpoint call
/*
INSERT INTO integration_steps (id, flow_id, step_order, step_type, config) 
VALUES (5, 1, 5, 'rest', '{"url": "http://localhost:8081/api/receive"}');
*/

-- Sample data for app_source
INSERT INTO app_source (id, name) VALUES
(1, 'John Doe'),
(2, 'Jane Smith'),
(3, 'Bob Johnson'),
(4, 'Alice Brown'),
(5, 'Charlie Wilson');

-- Initial data for staging_table (empty initially, will be populated by the integration flow)
-- Uncomment below if you want to test with some initial data
/*
INSERT INTO staging_table (id, name) VALUES
(1, 'John Doe'),
(2, 'Jane Smith');
*/