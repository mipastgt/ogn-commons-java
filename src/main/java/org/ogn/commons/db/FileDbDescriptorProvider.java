/**
 * Copyright (c) 2014 OGN, All Rights Reserved.
 */

package org.ogn.commons.db;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.ogn.commons.beacon.AircraftDescriptor;
import org.ogn.commons.beacon.descriptor.AircraftDescriptorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A FileDbDescriptorProvider is a type of <code>AircraftDescriptorProvider</code> which resolves AircraftDescriptors
 * from file-based databases (e.g. OGN ddb). It can be configured to refresh its internal cache periodically.
 * 
 * @author wbuczak
 */
public class FileDbDescriptorProvider<T extends FileDb> implements AircraftDescriptorProvider {

	private static final Logger LOG = LoggerFactory.getLogger(FileDbDescriptorProvider.class);

	private T db;
	private ScheduledExecutorService scheduledExecutor;

	private final int dbRefreshInterval;

	// default refresh rate (in sec.)
	private static final int DEFAULT_DB_INTERVAL = 60 * 60;

	public FileDbDescriptorProvider(Class<T> clazz, String dbFileUri, int dbRefreshInterval) {
		this.dbRefreshInterval = dbRefreshInterval;
		try {
			db = clazz.getConstructor(String.class).newInstance(dbFileUri);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			LOG.error("instantiation of descriptor provider failed!", e);
			return;
		}

		// load the first time
		db.reload();

		scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

		scheduledExecutor.scheduleAtFixedRate(() -> {
			LOG.debug("reloading db {}", db.getClass().getName());
			db.reload();
		}, dbRefreshInterval, dbRefreshInterval, TimeUnit.SECONDS);

	}

	public FileDbDescriptorProvider(Class<T> clazz, int dbRefreshInterval) {
		this(clazz, null, dbRefreshInterval);
	}

	public FileDbDescriptorProvider(Class<T> clazz) {
		this(clazz, null, DEFAULT_DB_INTERVAL);
	}

	@PostConstruct
	private void logConf() {
		LOG.info("created aircraft desciptor privider [uri: {}, refresh-interval: {} s, class: {}]", db.getUrl(),
				dbRefreshInterval, db.getClass().getCanonicalName());
	}

	@Override
	public Optional<AircraftDescriptor> findDescriptor(String address) {
		LOG.trace("entering findDescriptor()..");
		return db.getDescriptor(address);
	}

}