CREATE TABLE ppk_payments.clients (
  id varchar(36) PRIMARY KEY,
  name varchar(36) NOT NULL,
  status varchar(50) DEFAULT NULL,
  gateway_account_id varchar(10) DEFAULT NULL,
  gateway_merchant_id varchar(10) DEFAULT NULL,
  gateway_api_key varchar(36) DEFAULT NULL,
  gateway_api_login varchar(36) DEFAULT NULL,
  create_date TIMESTAMP DEFAULT NOW(),
  update_date TIMESTAMP DEFAULT NOW()
);

CREATE INDEX payments_client_name ON ppk_payments.clients(name);

CREATE TABLE ppk_payments.keys (
  id varchar(2000) PRIMARY KEY,
  client_id varchar(50) NOT NULL,
  status varchar(50) DEFAULT NULL,
  expiration_date TIMESTAMP,
  create_date TIMESTAMP DEFAULT NOW(),
  update_date TIMESTAMP DEFAULT NOW(),
  FOREIGN KEY (client_id) REFERENCES ppk_payments.clients (id)
);

CREATE INDEX payments_keys_client_id ON ppk_payments.keys(status);

CREATE TABLE ppk_payments.loads (
  id varchar(36) PRIMARY KEY,
  client_id varchar(36) NOT NULL,
  customer_id varchar(36) NOT NULL,
  amount float NOT NULL,
  currency varchar(3) NOT NULL,
  payer_name varchar(36) DEFAULT NULL,
  payer_card_last_digits varchar(36) DEFAULT NULL,
  method varchar(20) DEFAULT NULL,
  order_id varchar(50) DEFAULT NULL,
  transaction_id varchar(50) DEFAULT NULL,
  status varchar(50) NOT NULL,
  network_code varchar(100) DEFAULT NULL,
  network_message varchar(100) DEFAULT NULL,
  trazability_code varchar(100) DEFAULT NULL,
  response_code varchar(100) DEFAULT NULL,
  country varchar(3) NOT NULL,
  create_date TIMESTAMP DEFAULT NOW(),
  update_date TIMESTAMP DEFAULT NOW(),
  FOREIGN KEY (client_id) REFERENCES ppk_payments.clients (id)
);

CREATE INDEX payments_buyer_id ON ppk_payments.loads(customer_id);
CREATE INDEX payments_transaction_id ON ppk_payments.loads(transaction_id);
CREATE INDEX payments_status ON ppk_payments.loads(status);

CREATE TABLE ppk_payments.services (
  id varchar(36) PRIMARY KEY,
  service_id varchar(36) NOT NULL,
  amount float NOT NULL,
  status varchar(50) DEFAULT NULL,
  create_date TIMESTAMP DEFAULT NOW(),
  update_date TIMESTAMP DEFAULT NOW()
);

CREATE INDEX payments_service_id ON ppk_payments.services(service_id);
CREATE INDEX payments_status ON ppk_payments.services(status);

CREATE TABLE ppk_payments.balances (
  id varchar(36) PRIMARY KEY,
  customer_id varchar(36) NOT NULL,
  balance float NOT NULL,
  status varchar(50) DEFAULT NULL,
  create_date TIMESTAMP DEFAULT NOW(),
  update_date TIMESTAMP DEFAULT NOW()
);

CREATE INDEX payments_customer_id ON ppk_payments.balances(customer_id);
CREATE INDEX payments_status ON ppk_payments.balances(status);