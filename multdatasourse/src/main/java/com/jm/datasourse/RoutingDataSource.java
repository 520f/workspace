package com.jm.datasourse;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource {

     @Override
     protected Object determineCurrentLookupKey() {
            return DataSourceHolder.getDataSourceType();
     }
}
