CREATE TABLE integration_flows (
    id INT PRIMARY KEY,
    name VARCHAR(100),
    schedule_cron VARCHAR(50),
    active BOOLEAN
);

CREATE TABLE integration_steps (
    id INT PRIMARY KEY,
    flow_id INT,
    step_order INT,
    step_type VARCHAR(50),      -- 'sql', 'processor', 'rest', etc.
    config JSON,                -- e.g., query, URL, bean name
    FOREIGN KEY (flow_id) REFERENCES integration_flows(id)
);

-- Create app_source table
CREATE TABLE app_source (
    id INT PRIMARY KEY,
    name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create staging_table
CREATE TABLE staging_table (
    id INT PRIMARY KEY,
    name VARCHAR(100),
    processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE column_mappings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    flow_id INT,
    source_column VARCHAR(255),
    target_column VARCHAR(255),
    transform_expression VARCHAR(1000),
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (flow_id) REFERENCES integration_flows(id)
);