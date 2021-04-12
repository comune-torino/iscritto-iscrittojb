package it.csi.iscritto.iscrittojb.integration.dao.impl;

import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_BEGIN;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_END;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_ERROR;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.buildMessage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import it.csi.iscritto.iscrittojb.exception.DaoException;
import it.csi.iscritto.iscrittojb.integration.config.DBConfig;
import it.csi.iscritto.iscrittojb.integration.dao.AbstractDao;
import it.csi.iscritto.iscrittojb.integration.dao.InvioNotificaDao;
import it.csi.iscritto.iscrittojb.integration.dao.dto.NotificaRow;
import it.csi.iscritto.iscrittojb.util.DateUtils;
import it.csi.iscritto.iscrittojb.util.Joiner;

public class InvioNotificaDaoImpl extends AbstractDao implements InvioNotificaDao {
	private static final Logger log = Logger.getLogger(InvioNotificaDaoImpl.class);

	public InvioNotificaDaoImpl(DBConfig dbConfig) {
		super(dbConfig);
	}

	@Override
	public List<NotificaRow> findNotificheDaInviare() throws DaoException {
		final String methodName = "findNotificheDaInviare";
		log.debug(buildMessage(getClass(), methodName, LOG_BEGIN));

		final String query = "" +
				"select " +
				"  sms.id_invio_sms, " +
				"  sms.id_invio_massivo, " +
				"  sms.testo, " +
				"  sms.id_domanda_iscrizione, " +
				"  sms.dt_inserimento, " +
				"  sms.dt_invio, " +
				"  sms.esito, " +
				"  sms.telefono, " +
				"  ( " +
				"    select sog.codice_fiscale " +
				"    from iscritto_t_anagrafica_sog sog " +
				"      join iscritto_r_soggetto_rel r on sog.id_anagrafica_soggetto = r.id_anagrafica_soggetto " +
				"      join iscritto_d_tipo_sog tip on r.id_tipo_soggetto = tip.id_tipo_soggetto " +
				"    where sog.id_domanda_iscrizione = sms.id_domanda_iscrizione and tip.cod_tipo_soggetto = 'RIC' " +
				"  ) as codice_fiscale_richiedente " +
				"from iscritto_t_invio_sms sms " +
				"where dt_invio is null ";

		List<NotificaRow> result = new ArrayList<>();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			this.openConnection();
			ps = this.db.prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next()) {
				NotificaRow dto = new NotificaRow();

				dto.setIdInvioSms(rs.getLong("id_invio_sms"));
				dto.setIdInvioMassivo(rs.getLong("id_invio_massivo"));
				dto.setTesto(rs.getString("testo"));
				dto.setIdDomandaIscrizione(rs.getLong("id_domanda_iscrizione"));
				dto.setDtInserimento(DateUtils.toDate(rs.getTimestamp("dt_inserimento")));
				dto.setDtInvio(DateUtils.toDate(rs.getTimestamp("dt_invio")));
				dto.setEsito(rs.getString("esito"));
				dto.setTelefono(rs.getString("telefono"));
				dto.setCodiceFiscaleRichiedente(rs.getString("codice_fiscale_richiedente"));

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

	@Override
	public void aggiornaNotifiche(List<Long> ids, Long idInvioMassivo) throws DaoException {
		final String methodName = "aggiornaNotifiche";
		log.debug(buildMessage(getClass(), methodName, LOG_BEGIN));

		if (CollectionUtils.isEmpty(ids)) {
			log.info(buildMessage(getClass(), methodName, "nessuna notifica da aggiornare"));
			return;
		}

		final String query = String.format("" +
				"update iscritto_t_invio_sms set " +
				"  dt_invio = now(), " +
				"  id_invio_massivo = ? " +
				"where id_invio_sms in (%s) ", Joiner.on(", ").join(ids));

		PreparedStatement ps = null;
		try {
			this.openConnection();
			ps = this.db.prepareStatement(query);
			ps.setLong(1, idInvioMassivo);

			ps.execute();
			this.commit();
		}
		catch (Exception e) {
			log.error(buildMessage(getClass(), methodName, LOG_ERROR), e);
			this.rollback();

			throw new DaoException(e);
		}
		finally {
			closeStatement(ps);
			this.closeConnection();
		}

		log.debug(buildMessage(getClass(), methodName, LOG_END));
	}

	@Override
	public Long logInvioMassivo(String json, Integer statusCode) throws DaoException {
		final String methodName = "logInvioMassivo";
		log.debug(buildMessage(getClass(), methodName, LOG_BEGIN));

		final String query = "" +
				"insert into iscritto_t_log_notifica ( " +
				"  id_invio_massivo, " +
				"  payload, " +
				"  dt_invio, " +
				"  status_code " +
				") " +
				"VALUES( " +
				"  ?, " +
				"  ?, " +
				"  now(), " +
				"  ? " +
				") ";

		Long seq = this.getInvioMassivoSequence();

		PreparedStatement ps = null;
		try {
			this.openConnection();
			ps = this.db.prepareStatement(query);
			ps.setLong(1, seq);
			ps.setString(2, json);
			ps.setInt(3, statusCode);

			ps.execute();
			this.commit();
		}
		catch (Exception e) {
			log.error(buildMessage(getClass(), methodName, LOG_ERROR), e);
			this.rollback();

			throw new DaoException(e);
		}
		finally {
			closeStatement(ps);
			this.closeConnection();
		}

		log.debug(buildMessage(getClass(), methodName, LOG_END));
		return seq;
	}

	//////////////////////////////////////////////////////////////////////

	private Long getInvioMassivoSequence() throws DaoException {
		final String methodName = "getInvioMassivoSequence";
		log.debug(buildMessage(getClass(), methodName, LOG_BEGIN));

		final String query = "" +
				"select nextval('iscritto_t_log_notifica_id_invio_massivo_seq') as seq ";

		Long seq = null;

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			this.openConnection();
			ps = this.db.prepareStatement(query);
			rs = ps.executeQuery();

			while (rs.next()) {
				seq = rs.getLong("seq");
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
		return seq;
	}

}
