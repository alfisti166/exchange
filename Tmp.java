
/** SQL used to fetch all keys for given target and language. */
    private static final String FETCH_KEYS_SQL =
        "SELECT LS_ID FROM V_MEX_LANGUAGE_STRING WHERE LS_LG_ID = ? AND LS_TARGET = ?";

/* (non-Javadoc)
     * @see java.util.ResourceBundle#handleKeySet()
     */
    @Override
    protected Set<String> handleKeySet() {
        Set<String> keys = null;
        try {
DEBUG_LOG.entering("pl.nordea.mex.dms.model.MexResourceBundleBase",
                               "handleKeySet");
            keys = doInSql(new SQLCallback<Set<String>>() {
                        @Override
                        public Set<String> doCall(Connection conn) throws SQLException {
                            PreparedStatement ps =
conn.prepareStatement(FETCH_KEYS_SQL);
                            ps.setString(1, getLang());
                            ps.setString(2, getTarget());
                            HashSet<String> keys = new HashSet<String>();
                            ResultSet rs = ps.executeQuery();
                            while (rs.next()) {
                                String k = rs.getString(1);
                                keys.add(k);
                            }
                            ps.close();
                            return keys;
                        }
                    });
            return keys;
        } catch (Exception e) {
            LOG.error("Error: {}", e);
            AUDIT_LOG.log(AUDIT_LOG.ERROR, "Error: {}", e);
        } finally {
DEBUG_LOG.exiting("pl.nordea.mex.dms.model.MexResourceBundleBase",
                              "handleKeySet", keys);
        }
        return keys;
    }

/**
     * Do in sql.
     *
     * @param <T> the generic type
     * @param cb the cb
     * @return the t
     * @throws NamingException the naming exception
     * @throws SQLException the sQL exception
     */
    private static <T> T doInSql(SQLCallback<T> cb) throws NamingException,
SQLException {

        InitialContext ctx = new InitialContext();
        try {
DEBUG_LOG.entering("pl.nordea.mex.dms.model.MexResourceBundleBase",
                               "doInSql");
            DataSource ds = (DataSource)ctx.lookup(MEX_JDBC_RESOURCE);
            Connection conn = ds.getConnection();
            try {
                return cb.doCall(conn);
            } catch (Exception e) {
                LOG.error("Error: {}", e);
                AUDIT_LOG.log(AUDIT_LOG.ERROR, "Error: {}", e);
            } finally {
                conn.close();
            }
        } catch (Exception e) {
            LOG.error("Error: {}", e);
            AUDIT_LOG.log(AUDIT_LOG.ERROR, "Error: {}", e);
        } finally {
            ctx.close();
DEBUG_LOG.exiting("pl.nordea.mex.dms.model.MexResourceBundleBase",
                              "doInSql", null);
        }
        return null;
    }

    /**
     * The Interface SQLCallback.
     *
     * @param <T> the generic type
     * @author Artur Krupa, AMG.net
     */
    private static interface SQLCallback<T> {

        /**
         * Do call.
         *
         * @param conn the conn
         * @return the t
         * @throws SQLException the sQL exception
         */
        T doCall(Connection conn) throws SQLException;
    }



