
-- Test servicelog_entry function
select * from servicelog_entry('Cascade', 'L1', 'A2CEngine', 'Running', 'sgoutham')
	

-- Test servicelog_details function
select * from servicelog_details('Cascade', 'L1', 'A2CEngine')

-- Test users_entry function
select * from users_entry('gouthamraj@xome.com', 'xome123')
select * from users

-- Test check_password
select check_password('gouthamraj@xome.com', 'xome123')

-- Test servicelogadvanced_entry function
select * from servicelogadvanced_entry('Cascade', 'L2', 'A2CEngine', '0', 'sgoutham')
select * from servicelogadvanced

-- Test servicelogadvanced_details function
select * from servicelogadvanced_details('Cascade', 'L2', 'A2CEngine')

-- Test get_userpreferences function
select * from get_userpreferences('gouthamraj@xome.com')

-- Test add_userpreference function
select add_userpreference('gouthamraj@xome.com', 'A2CEngine')

-- Test delete_userpreference function
select delete_userpreference('gouthamraj@xome.com', 'A2CEngine')
