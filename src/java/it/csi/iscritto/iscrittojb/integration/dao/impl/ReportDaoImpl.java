package it.csi.iscritto.iscrittojb.integration.dao.impl;

import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_BEGIN;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_END;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_ERROR;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.buildMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import it.csi.iscritto.iscrittojb.exception.DaoException;
import it.csi.iscritto.iscrittojb.integration.config.DBConfig;
import it.csi.iscritto.iscrittojb.integration.dao.AbstractDao;
import it.csi.iscritto.iscrittojb.integration.dao.ReportDao;
import it.csi.iscritto.iscrittojb.integration.dao.dto.DatiScuola;
import it.csi.iscritto.iscrittojb.integration.dao.dto.DatiStepRow;

public class ReportDaoImpl extends AbstractDao implements ReportDao {
	private static final Logger log = Logger.getLogger(ReportDaoImpl.class);

	public ReportDaoImpl(DBConfig dbConfig) {
		super(dbConfig);
	}

	@Override
	public Connection openAndGetNewConnection() throws DaoException {
		return this.openAndGetConnection();
	}

	@Override
	public void closeNewConnection(Connection connection) {
		closeExternalConnection(connection);
	}

	@Override
	public DatiStepRow findDatiStep(String codOrdineScuola, String codStatoGra) throws DaoException {
		final String methodName = "findDatiStep";
		log.debug(buildMessage(getClass(), methodName, LOG_BEGIN));

		final String query = "" +
				"select " +
				"  sgc.id_step_gra_con, " +
				"  ag.id_anno_scolastico " +
				"from " +
				"  iscritto_t_step_gra sg, " +
				"  iscritto_t_step_gra_con sgc, " +
				"  iscritto_t_anagrafica_gra ag, " +
				"  iscritto_d_ordine_scuola os, " +
				"  iscritto_d_stato_gra s " +
				"where " +
				"  sg.id_step_gra = sgc.id_step_gra " +
				"  and sg.id_anagrafica_gra = ag.id_anagrafica_gra " +
				"  and ag.id_ordine_scuola = os.id_ordine_scuola " +
				"  and sgc.id_stato_gra = s.id_stato_gra " +
				"  and os.cod_ordine_scuola = ? " +
				"  and s.cod_stato_gra= ? " +
				"order by sgc.dt_step_con desc ";

		DatiStepRow result = null;

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			this.openConnection();
			ps = this.db.prepareStatement(query);

			ps.setString(1, codOrdineScuola);
			ps.setString(2, codStatoGra);

			rs = ps.executeQuery();
			while (rs.next()) {
				result = new DatiStepRow();
				result.setIdStep(rs.getLong("id_step_gra_con"));
				result.setIdAnnoScolastico(rs.getLong("id_anno_scolastico"));

				break;
			}
		}
		catch (Exception e) {
			log.error(buildMessage(getClass(), methodName, LOG_ERROR), e);
			this.rollback();

			throw new DaoException(e);
		}
		finally {
			closeResultSet(rs);
			closeStatement(ps);
			this.closeConnection();
		}

		log.debug(buildMessage(getClass(), methodName, LOG_END));
		return result;
	}


	@Override
	public List<DatiScuola> findDatiScuole(Long idAnnoScolastico, String codOrdineScuola) throws DaoException {
		final String methodName = "findDatiScuole";

		log.debug(buildMessage(getClass(), methodName, LOG_BEGIN));

		final String query = "" +
				"select distinct " +
				"  c.id_scuola, " +
				"  s.cod_scuola, " +
				"  c.id_tipo_frequenza, " +
				"  left(tf.cod_tipo_frequenza, 1) tf " +
				"from " +
				"  iscritto_t_classe c, " +
				"  iscritto_t_scuola s, " +
				"  iscritto_d_ordine_scuola os, " +
				"  iscritto_d_tipo_fre tf " +
				"where c.id_scuola = s.id_scuola " +
				"  and c.id_tipo_frequenza = tf.id_tipo_frequenza " +
				"  and c.id_anno_scolastico = ? " +
				"  and os.id_ordine_scuola = s.id_ordine_scuola " +
				"  and os.cod_ordine_scuola = ? " +
				"order by c.id_scuola, c.id_tipo_frequenza ";

		List<DatiScuola> result = new ArrayList<>();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			this.openConnection();
			ps = this.db.prepareStatement(query);

			ps.setLong(1, idAnnoScolastico);
			ps.setString(2, codOrdineScuola);

			rs = ps.executeQuery();
			while (rs.next()) {
				DatiScuola dto = new DatiScuola();

				dto.setIdScuola(rs.getLong("id_scuola"));
				dto.setCodScuola(rs.getString("cod_scuola"));
				dto.setIdTipoFrequenza(rs.getLong("id_tipo_frequenza"));
				dto.setTipoFrequenza(rs.getString("tf"));

				result.add(dto);
			}
		}
		catch (Exception e) {
			log.error(buildMessage(getClass(), methodName, LOG_ERROR), e);
			this.rollback();

			throw new DaoException(e);
		}
		finally {
			closeResultSet(rs);
			closeStatement(ps);
			this.closeConnection();
		}

		log.debug(buildMessage(getClass(), methodName, LOG_END));
		return result;
	}

}
