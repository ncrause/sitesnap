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
