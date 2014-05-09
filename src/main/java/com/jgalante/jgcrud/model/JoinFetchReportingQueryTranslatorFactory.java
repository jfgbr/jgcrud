package com.jgalante.jgcrud.model;

import java.util.Map;

import org.hibernate.engine.query.spi.EntityGraphQueryHint;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.spi.FilterTranslator;
import org.hibernate.hql.spi.QueryTranslator;
import org.hibernate.hql.spi.QueryTranslatorFactory;

/**
 * A query translator factory to be set in the hibernate configuration to report the usage of JOIN FETCH with limit.
 *
 * @author Lukas Krejci
 */
@SuppressWarnings("rawtypes")
public class JoinFetchReportingQueryTranslatorFactory implements QueryTranslatorFactory {
	
	public QueryTranslator createQueryTranslator(String queryIdentifier, String queryString, Map filters,
        SessionFactoryImplementor factory) {
        return new JoinFetchReportingQueryTranslator(queryIdentifier, queryString, filters, factory);
    }

    @Override
    public FilterTranslator createFilterTranslator(String queryIdentifier, String queryString, Map filters,
        SessionFactoryImplementor factory) {
        return new JoinFetchReportingQueryTranslator(queryIdentifier, queryString, filters, factory);
    }

	@Override
	public QueryTranslator createQueryTranslator(String queryIdentifier,
			String queryString, Map filters, SessionFactoryImplementor factory,
			EntityGraphQueryHint entityGraphQueryHint) {
		return createQueryTranslator(queryIdentifier, queryString, filters, factory);
	}
}
