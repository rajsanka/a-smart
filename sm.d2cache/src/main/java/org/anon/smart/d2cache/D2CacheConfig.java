/**
 * SMART - State Machine ARchiTecture
 *
 * Copyright (C) 2012 Individual contributors as indicated by
 * the @authors tag
 *
 * This file is a part of SMART.
 *
 * SMART is a free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SMART is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * */
 
/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.anon.smart.d2cache.D2CacheConfig
 * Author:              vjaasti
 * Revision:            1.0
 * Date:                Apr 2, 2013
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * <Purpose>
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache;

import org.anon.smart.d2cache.store.StoreConfig;
public interface D2CacheConfig {

	public void createMemoryConfig();
	public void createIndexConfig(String indexHome);
	public void createStoreConfig(String zookeeper, String zookeeperPort, String hbaseHost, boolean isLocal);
	public void createHadoopStoreConfig();
	public void createDiskStoreConfig();
    public void createMySQLConfig();
	
    public boolean useRDB();
	public StoreConfig getMemoryConfig();
	public StoreConfig getIndexConfig();
	public StoreConfig getStoreConfig();
	public StoreConfig getDiskStoreConfig();
	public StoreConfig getHadoopStoreConfig();
    public StoreConfig getMySQLConfig();
}
