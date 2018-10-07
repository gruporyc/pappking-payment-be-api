CREATE TRIGGER clients_update_trigger BEFORE UPDATE ON ppk_payments.clients FOR EACH ROW SET new.update_date = NOW();

CREATE TRIGGER keys_update_trigger BEFORE UPDATE ON ppk_payments.keys FOR EACH ROW SET new.update_date = NOW();

CREATE TRIGGER loads_update_trigger BEFORE UPDATE ON ppk_payments.loads FOR EACH ROW SET new.update_date = NOW();

CREATE TRIGGER services_update_trigger BEFORE UPDATE ON ppk_payments.services FOR EACH ROW SET new.update_date = NOW();

CREATE TRIGGER balances_update_trigger BEFORE UPDATE ON ppk_payments.balances FOR EACH ROW SET new.update_date = NOW();