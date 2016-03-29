-- Function for inserting service log
create or replace function servicelog_entry(_product text, _environment text, _service text, _status text, _createdby text)
	returns void as $$
		begin
			insert into servicelog (product, environment, service, status, createdby, created)
			values (_product, _environment, _service, _status, _createdby, CURRENT_TIMESTAMP);
		end;
$$ LANGUAGE 'plpgsql';


-- Function for reteriving service logs for an single service
create or replace function servicelog_details(_product text, _environment text, _service text)
	returns table(entryTime text, status text) as $$
		begin
			return query
				select 
					* 
				from
					(select 
						to_char(servicelog.created, 'Mon DD, YYYY HH12:MI:SS am') as entryTime, servicelog.status 
					from 
						servicelog 
					where 
						product = _product and 
						environment = _environment and
						service = _service and
						deleted is null
					order by
						servicelog.created desc) as tmp
				limit
					10;
		end;
$$ LANGUAGE 'plpgsql';

-- Function to register new user
create or replace function users_entry(_username text, _password text)
	returns void as $$
		begin
			insert into users (username, password_hash, createdby, created)
			values (_username, md5(_password), _username, CURRENT_TIMESTAMP);
		end;
$$ LANGUAGE 'plpgsql';

-- Function to verify login
CREATE or replace FUNCTION check_password(uname TEXT, pass TEXT)
RETURNS BOOLEAN AS $$
DECLARE passed BOOLEAN := 0;
declare password_md5 text;
BEGIN
	select md5($2) into password_md5;
        SELECT  (password_hash = password_md5) INTO passed
        FROM    public.users
        WHERE   username = $1;

        RETURN passed;
END;
$$  LANGUAGE plpgsql
    SECURITY DEFINER
    -- Set a secure search_path: trusted schema(s), then 'pg_temp'.
    SET search_path = admin, pg_temp;

-- Function for inserting service log
create or replace function servicelogadvanced_entry(_product text, _environment text, _service text, _status text, _createdby text)
	returns void as $$
		begin
			insert into servicelogadvanced (product, environment, service, status, createdby, created)
			values (_product, _environment, _service, _status, _createdby, CURRENT_TIMESTAMP);
		end;
$$ LANGUAGE 'plpgsql';

-- Function for reteriving advanced service logs for an single service
create or replace function servicelogadvanced_details(_product text, _environment text, _service text)
	returns table(entryTime text, status text) as $$
		begin
			return query
				select
					* 
				from
					(select 
						to_char(servicelogadvanced.created, 'YYYY-MM-DD HH24:MI:SS') as entryTime, servicelogadvanced.status 
					from 
						servicelogadvanced 
					where 
						product = _product and 
						environment = _environment and
						service = _service and
						deleted is null
					order by
						servicelogadvanced.created desc) as tmp
				limit
					10;
		end;
$$ LANGUAGE 'plpgsql';

-- Function for reteriving the user preference
create or replace function get_userpreferences(_username text)
	returns table(services text) as $$
		begin
			return query
				select
					userpreferences.service
				from
					userpreferences inner join users on users.id = userid
				where
					users.username = _username
					and users.deleted is null
					and userpreferences.deleted is null;
		end;
$$ LANGUAGE 'plpgsql';

-- Function for adding the user preference
create or replace function add_userpreference(_username text, _service text)
	returns void as $$
		declare
			_userid bigint;
		begin
			select id::bigint from users where username = _username into _userid;
			insert into userpreferences (userid, service, createdby, created)
			values (_userid, _service, _username, CURRENT_TIMESTAMP);
		end;
$$ LANGUAGE 'plpgsql';

-- Function for deleting the user preference
create or replace function delete_userpreference(_username text, _service text)
	returns void as $$
		declare
			_userid bigint;
		begin
			select id::bigint from users where username = _username into _userid;
			update 
				userpreferences 
			set 
				deletedby = _username,
				deleted = CURRENT_TIMESTAMP
			where
				userid = _userid;
		end;
$$ LANGUAGE 'plpgsql';