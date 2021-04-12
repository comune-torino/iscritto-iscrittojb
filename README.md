# Project Title
ISCRITTO-ISCRITTOJB

# Project Description
Questa componente è un batch per le notifiche ed i report PDF.

# Configurations
Aprire il file buildfiles/dev-rp-01.properties e configurare i seguenti parametri:
- stringaConnessioneDatabase: url di configurazione Datasource - jdbc:postgresql://HOST:PORT/DATABASE;
- username: utente db;
- password: password db;
- notificatore.ws.messagebroker.url: url del servizio REST per l'invio delle notifiche;
- notificatore.ws.messagebroker.tokenjwt: x-authentication per contattare il notificatore;
- nao.service.url: url dei servizi di fruizione della Nuova anagrafe open;
- nao.service.user: user dei servizi di fruizione della Nuova anagrafe open;
- nao.service.password: password dei servizi di fruizione della Nuova anagrafe open;

# Getting Started 
Clonare la componente iscritto-iscrittojb dal repository git 
Modificare i file di configurazione e compilare il progetto. 

# Prerequisites
I prerequisiti per l'installazione della componente sono i seguenti:
## Software
- [JDK 8](https://www.apache.org)
- [PostgreSQL 9.6.11](https://www.postgresql.org/download/)  

- Tutte le librerie elencate nel BOM.csv devono essere accessibili per compilare il progetto. Le librerie sono pubblicate su http://repart.csi.it, ma per semplicità sono state incluse nella cartella lib/.

# Versioning
Per la gestione del codice sorgente viene utilizzata Git. Per la gestione del versioning si fa riferimento alla metodologia [Semantic Versioning](https://semver.org/).

# Copyrights
(C) Copyright 2021 Città di Torino

# License
Questo software è distribuito con licenza EUPL-1.2
Consultare i file EUPL v1_2 IT-LICENSE.txt e EUPL v1_2 EN-LICENSE.txt per maggiori dettagli.

