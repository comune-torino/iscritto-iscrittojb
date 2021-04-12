package it.csi.iscritto.iscrittojb.integration.dao;

import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_BEGIN;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_END;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_ERROR;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.buildMessage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import it.csi.iscritto.iscrittojb.exception.DaoException;
import it.csi.iscritto.iscrittojb.integration.config.DBConfig;

public abstract class AbstractDao {
	private static final Logger log = Logger.getLogger(AbstractDao.class);

	private DBConfig dbConfig;
	protected Connection db;

	protected AbstractDao(DBConfig dbConfig) {
		this.dbConfig = dbConfig;

		if (this.dbConfig == null) {
			throw new IllegalArgumentException("dbConfig non valido");
		}

		if (StringUtils.isBlank(this.dbConfig.getDbUrl())) {
			throw new IllegalArgumentException("dbUrl non valido");
		}

		if (StringUtils.isBlank(this.dbConfig.getDbUser())) {
			throw new IllegalArgumentException("dbUser non valido");
		}

		if (StringUtils.isBlank(this.dbConfig.getDbPassword())) {
			throw new IllegalArgumentException("dbPassword non valido");
		}
	}

	public final void openConnection() throws DaoException {
		final String methodName = "openConnection";
		log.debug(buildMessage(getClass(), methodName, LOG_BEGIN));

		try {
			if (this.db == null || this.db.isClosed()) {
				this.db = DriverManager.getConnection(
						this.dbConfig.getDbUrl(),
						this.dbConfig.getDbUser(),
						this.dbConfig.getDbPassword());

				this.db.setAutoCommit(false);
				this.db.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

				log.debug(buildMessage(getClass(), methodName, "openConnection: OK"));
			}
		}
		catch (SQLException e) {
			log.error(buildMessage(getClass(), methodName, LOG_ERROR), e);
			new DaoException(e);
		}

		log.debug(buildMessage(getClass(), methodName, LOG_END));
	}

	public final void closeConnection() {
		final String methodName = "closeConnection";
		log.debug(buildMessage(getClass(), methodName, LOG_BEGIN));

		if (this.db != null) {
			try {
				this.db.close();
				log.debug(buildMessage(getClass(), methodName, "closeConnection: OK"));
			}
			catch (SQLException e) {
				log.error(buildMessage(getClass(), methodName, LOG_ERROR), e);
			}
		}

		log.debug(buildMessage(getClass(), methodName, LOG_END));
	}

	public final void commit() throws DaoException {
		final String methodName = "commit";

		if (this.db != null) {
			try {
				this.db.commit();
			}
			catch (SQLException e) {
				log.error(buildMessage(getClass(), methodName, LOG_ERROR), e);
				new DaoException(e);
			}
		}
	}

	public final void rollback() throws DaoException {
		final String methodName = "rollback";

		if (this.db != null) {
			try {
				this.db.rollback();
			}
			catch (SQLException e) {
				log.error(buildMessage(getClass(), methodName, LOG_ERROR), e);
				new DaoException(e);
			}
		}
	}

	protected static final void closeResultSet(final ResultSet rs) {
		final String methodName = "closeResultSet";

		if (rs != null) {
			try {
				rs.close();
			}
			catch (SQLException e) {
				log.error(buildMessage(AbstractDao.class, methodName, LOG_ERROR), e);
			}
		}
	}

	protected static final void closeStatement(final Statement st) {
		final String methodName = "closeStatement";

		if (st != null) {
			try {
				st.close();
			}
			catch (SQLException e) {
				log.error(buildMessage(AbstractDao.class, methodName, LOG_ERROR), e);
			}
		}
	}

	protected final Connection openAndGetConnection() {
		final String methodName = "openAndGetConnection";
		log.debug(buildMessage(getClass(), methodName, LOG_BEGIN));

		Connection connection = null;
		try {
			connection = DriverManager.getConnection(
					this.dbConfig.getDbUrl(),
					this.dbConfig.getDbUser(),
					this.dbConfig.getDbPassword());

			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

			log.debug(buildMessage(getClass(), methodName, "openAndGetConnection: OK"));
		}
		catch (SQLException e) {
			log.error(buildMessage(getClass(), methodName, LOG_ERROR), e);
			new DaoException(e);
		}

		log.debug(buildMessage(getClass(), methodName, LOG_END));
		return connection;
	}

	protected static final void closeExternalConnection(Connection connection) {
		final String methodName = "closeExternalConnection";
		log.debug(buildMessage(AbstractDao.class, methodName, LOG_BEGIN));

		if (connection != null) {
			try {
				connection.close();
			}
			catch (SQLException e) {
				log.error(buildMessage(AbstractDao.class, methodName, LOG_ERROR), e);
			}
		}

		log.debug(buildMessage(AbstractDao.class, methodName, LOG_END));
	}

}
