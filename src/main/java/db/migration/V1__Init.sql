/* 
 * Copyright (C) 2019 Nathan Crause <nathan@crause.name>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * Author:  Nathan Crause <nathan@crause.name>
 * Created: 15-Oct-2019
 */

-- Stores actual snapshots taken
create table snaps(
	id bigserial primary key,
	date_created ${timestamp_type} not null default current_timestamp,
	target_url text not null,
	via varchar(255) not null
		check (via = 'web' or via = 'api'),
	photo ${blob_type}
);

-- Store information about snapshots requested via the web
create table requests(
	id bigserial primary key,
	snap_id bigint not null
		references snaps(id)
			on update cascade
			on delete cascade,
	date_created ${timestamp_type} not null default current_timestamp,
	remote_ip ${network_addr_type} not null,
	-- nullable API user ID for requests coming in via API
	api_user_id bigint
);

-- Stores header and parameter data pertaining to a web request
create table request_data(
	id bigserial primary key,
	request_id bigint not null
		references requests(id)
			on update cascade
			on delete cascade,
	date_created ${timestamp_type} not null default current_timestamp,
	type varchar(255) not null
		check (type = 'parameter' or type = 'header'),
	name varchar(255) not null,
	value text
);

-- Top level "header" for all API packages
create table packages(
	id bigserial primary key,
	name varchar(255) not null,
	date_created ${timestamp_type} not null default current_timestamp
);

-- For now, only need one free package
insert into packages (name) values ('Free');

set @package_id = (select max(id) from packages);

-- Individual time-based limits to be applied to packages
create table limits(
	id bigserial primary key,
	package_id bigint not null,
	temporal_unit varchar(32) not null
		check(temporal_unit in ('minute', 'hour', 'day', 'month')),
	temporal_amount int not null,
	usage_cap bigint not null,
	date_created ${timestamp_type} not null default current_timestamp
);

-- basic limits for the free package
insert into limits (package_id, temporal_unit, temporal_amount, usage_cap)
	values(@package_id, 'minute', 1, 10);
insert into limits (package_id, temporal_unit, temporal_amount, usage_cap)
	values(@package_id, 'hour', 1, 500);
insert into limits (package_id, temporal_unit, temporal_amount, usage_cap)
	values(@package_id, 'day', 1, 10000);
insert into limits (package_id, temporal_unit, temporal_amount, usage_cap)
	values(@package_id, 'month', 1, 250000);

-- All the users capable of using the API
create table api_users(
	id bigserial primary key,
	email_address varchar(255) not null unique,
	password_hash char(64) not null,
	package_id bigint not null,
	date_created ${timestamp_type} not null default current_timestamp,
);

-- Just me - password is "change"
insert into api_users (email_address, password_hash, package_id)
	values ('nathan@crause.name', '057ba03d6c44104863dc7361fe4578965d1887360f90a0895882e58a6248fc86', @package_id);
