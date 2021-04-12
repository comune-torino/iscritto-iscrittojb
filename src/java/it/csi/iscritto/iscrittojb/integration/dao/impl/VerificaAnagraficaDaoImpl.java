package it.csi.iscritto.iscrittojb.integration.dao.impl;

import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_BEGIN;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_END;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.LOG_ERROR;
import static it.csi.iscritto.iscrittojb.util.LoggingUtils.buildMessage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jfree.util.Log;

import it.csi.iscritto.iscrittojb.exception.DaoException;
import it.csi.iscritto.iscrittojb.integration.config.DBConfig;
import it.csi.iscritto.iscrittojb.integration.dao.AbstractDao;
import it.csi.iscritto.iscrittojb.integration.dao.VerificaAnagraficaDao;
import it.csi.iscritto.iscrittojb.integration.dao.dto.AnagraficaRow;
import it.csi.iscritto.iscrittojb.util.converter.ConvertUtils;

public class VerificaAnagraficaDaoImpl extends AbstractDao implements VerificaAnagraficaDao {
	private static final Logger log = Logger.getLogger(ReportDaoImpl.class);

	public VerificaAnagraficaDaoImpl(DBConfig dbConfig) {
		super(dbConfig);
	}

	@Override
	public List<AnagraficaRow> findAnagraficaSoggetti() throws DaoException {
		final String methodName = "findAnagraficaSoggetti";
		log.debug(buildMessage(getClass(), methodName, LOG_BEGIN));

		final String query = "" +
				"with " +
				"parametri as ( " +
				"  select " +
				"    coalesce(date_trunc('day', p.dt_ult_verifica_nao), to_date('2019-01-01', 'YYYY-MM-DD')) as data_ultimo_controllo " +
				"  from iscritto_t_parametro p  " +
				"  order by p.dt_ult_verifica_nao asc  " +
				"  limit 1  " +
				"), " +
				"elenco_domande as ( " +
				"  select " +
				"    dom.id_domanda_iscrizione " +
				"  from iscritto_t_domanda_isc dom " +
				"    join iscritto_d_stato_dom stdom on stdom.id_stato_dom = dom.id_stato_dom " +
				"  where 1 = 1  " +
				"    and stdom.cod_stato_dom = 'INV' " +
				"    and date_trunc('day', dom.data_consegna) between (select data_ultimo_controllo from parametri) and date_trunc('day', now()) " +
				"), " +
				"soggetti_domanda as ( " +
				"  select distinct " +
				"    dom.id_domanda_iscrizione, " +
				"    sog.id_anagrafica_soggetto, " +
				"    sog.codice_fiscale, " +
				"    sog.data_nascita, " +
				"    sog.id_rel_parentela, " +
				"    tip.cod_tipo_soggetto " +
				"  from elenco_domande dom " +
				"    join iscritto_t_anagrafica_sog sog on sog.id_domanda_iscrizione = dom.id_domanda_iscrizione " +
				"    join iscritto_r_soggetto_rel r on r.id_anagrafica_soggetto = sog.id_anagrafica_soggetto " +
				"    join iscritto_d_tipo_sog tip on r.id_tipo_soggetto = tip.id_tipo_soggetto " +
				"  order by " +
				"    dom.id_domanda_iscrizione, " +
				"    tip.cod_tipo_soggetto " +
				") " +
				"select " +
				"  sog_dom.id_domanda_iscrizione, " +
				"  sog_dom.id_anagrafica_soggetto, " +
				"  sog_dom.codice_fiscale, " +
				"  sog_dom.data_nascita, " +
				"  sog_dom.cod_tipo_soggetto " +
				"from " +
				"  soggetti_domanda sog_dom " +
				"where 1 = 1 " +
				"  and exists (  " +
				"  	select * from soggetti_domanda sd  " +
				"      join iscritto_t_indirizzo_res res on res.id_anagrafica_soggetto = sd.id_anagrafica_soggetto " +
				"      join iscritto_t_comune com on com.id_comune = res.id_comune " +
				"    where " +
				"      sd.id_domanda_iscrizione = sog_dom.id_domanda_iscrizione and " +
				"      com.desc_comune = 'TORINO' " +
				"  ) " +
				"order by " +
				"  sog_dom.id_domanda_iscrizione, " +
				"  sog_dom.cod_tipo_soggetto ";

		List<AnagraficaRow> result = new ArrayList<>();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			this.openConnection();
			ps = this.db.prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next()) {
				AnagraficaRow dto = new AnagraficaRow();

				dto.setIdDomandaIscrizione(rs.getLong("id_domanda_iscrizione"));
				dto.setIdAnagraficaSoggetto(rs.getLong("id_anagrafica_soggetto"));
				dto.setCodiceFiscale(rs.getString("codice_fiscale"));
				dto.setCodTipoSoggetto(rs.getString("cod_tipo_soggetto"));
				dto.setDataNascita(rs.getTimestamp("data_nascita"));

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
	public int updateFlagResidenzaNao(Long idSoggetto, Boolean flag) throws DaoException {
		final String methodName = "updateFlagResidenzaNao";
		log.debug(buildMessage(getClass(), methodName, LOG_BEGIN));

		final String query = "" +
				"update iscritto_t_anagrafica_sog set " +
				"  fl_residenza_nao = ? " +
				"where id_anagrafica_soggetto = ? ";

		PreparedStatement ps = null;
		try {
			this.openConnection();
			ps = this.db.prepareStatement(query);

			ps.setString(1, ConvertUtils.toSNN(flag)); // fl_residenza_nao
			ps.setLong(2, idSoggetto); // id_anagrafica_soggetto

			int result = ps.executeUpdate();
			this.commit();

			return result;
		}
		catch (Exception e) {
			log.error(buildMessage(getClass(), methodName, LOG_ERROR), e);
			this.rollback();

			throw new DaoException(e);
		}
		finally {
			closeStatement(ps);
			this.closeConnection();

			log.debug(buildMessage(getClass(), methodName, LOG_END));
		}
	}

	@Override
	public int updateIdCivicoNao(Long idSoggetto, Long idCivico) throws DaoException {
		final String methodName = "updateIdCivicoNao";
		log.debug(buildMessage(getClass(), methodName, LOG_BEGIN));

		Log.info("@@ eseguo l'update con id_anagrafica: "+idSoggetto+" e id civico: "+idCivico);



		final String query = "" +
				"update iscritto_t_indirizzo_res set " +
				"  id_civico = ? " +
				"where id_anagrafica_soggetto = ? ";

		PreparedStatement ps = null;
		try {
			this.openConnection();
			ps = this.db.prepareStatement(query);

			ps.setLong(1, idCivico); // fl_residenza_nao
			ps.setLong(2, idSoggetto); // id_anagrafica_soggetto

			int result = ps.executeUpdate();
			this.commit();

			return result;
		}
		catch (Exception e) {
			log.error(buildMessage(getClass(), methodName, LOG_ERROR), e);
			this.rollback();

			throw new DaoException(e);
		}
		finally {
			closeStatement(ps);
			this.closeConnection();

			log.debug(buildMessage(getClass(), methodName, LOG_END));
		}
	}

	@Override
	public int updateUltimaVerifica() throws DaoException {
		final String methodName = "updateUltimaVerifica";
		log.debug(buildMessage(getClass(), methodName, LOG_BEGIN));

		final String query = "" +
				"update iscritto_t_parametro " +
				"set dt_ult_verifica_nao = now() " +
				"where id_ordine_scuola = ( " +
				"    select " +
				"    p.id_ordine_scuola " +
				"    from iscritto_t_parametro p " +
				"    order by p.dt_ult_verifica_nao asc " +
				"    limit 1 " +
				"    ) ";

		PreparedStatement ps = null;
		try {
			this.openConnection();
			ps = this.db.prepareStatement(query);

			int result = ps.executeUpdate();
			this.commit();

			return result;
		}
		catch (Exception e) {
			log.error(buildMessage(getClass(), methodName, LOG_ERROR), e);
			this.rollback();

			throw new DaoException(e);
		}
		finally {
			closeStatement(ps);
			this.closeConnection();

			log.debug(buildMessage(getClass(), methodName, LOG_END));
		}
	}

}
