package com.airbus_cyber_security.graylog;

import com.airbus_cyber_security.graylog.AggregationCount;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.graylog2.alerts.AbstractAlertCondition;
import org.graylog2.indexer.results.ResultMessage;
import org.graylog2.indexer.results.SearchResult;
import org.graylog2.indexer.results.TermsResult;
import org.graylog2.indexer.searches.Searches;
import org.graylog2.indexer.searches.Sorting;
import org.graylog2.indexer.searches.Sorting.Direction;
import org.graylog2.plugin.Tools;
import org.graylog2.plugin.alarms.AlertCondition;
import org.graylog2.plugin.indexer.searches.timeranges.TimeRange;
import org.graylog2.plugin.streams.Stream;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AlertConditionTest {
    protected static final String alertConditionTitle = "Alert Condition for Testing";

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    protected Stream stream;
    @Mock
    protected Searches searches;

    private static final String STREAM_ID = "STREAMMOCKID";
    protected static final String STREAM_CREATOR = "MOCKUSER";
    protected static final String CONDITION_ID = "CONDITIONMOCKID";

    @Before
    public void setUp() throws Exception {
        when(stream.getId()).thenReturn(STREAM_ID);
    }

    protected void assertTriggered(AlertCondition alertCondition, AlertCondition.CheckResult result) {
        assertTrue("AlertCondition should be triggered, but it's not!", result.isTriggered());
        assertNotNull("Timestamp of returned check result should not be null!", result.getTriggeredAt());
        assertEquals("AlertCondition of result is not the same we created!", result.getTriggeredCondition(), alertCondition);
        long difference = Tools.nowUTC().getMillis() - result.getTriggeredAt().getMillis();
        assertTrue("AlertCondition should be triggered about now", difference < 1000);
    }

    protected void assertNotTriggered(AlertCondition.CheckResult result) {
        assertFalse("AlertCondition should not be triggered, but it is!", result.isTriggered());
        assertNull("No timestamp should be supplied if condition did not trigger", result.getTriggeredAt());
        assertNull("No triggered alert condition should be supplied if condition did not trigger", result.getTriggeredCondition());
    }

    protected Map<String, Object> getParametersMap(Integer grace, Integer time, AggregationCount.ThresholdType type, 
    		Number threshold, List<String> aggregatingFields, List<String> differentiatingFields, int backlog) {
    	Map<String, Object> parameters = Maps.newHashMap();
    	parameters.put("grace", grace);
    	parameters.put("time", time);
    	parameters.put("threshold", threshold);
    	parameters.put("backlog",backlog);
    	parameters.put("threshold_type", type.toString());
    	parameters.put("grouping_fields", aggregatingFields);
    	parameters.put("distinction_fields", differentiatingFields);

    	return parameters;
    }
    
    protected AggregationCount getAggregationCount(Map<String, Object> parameters, String title) {
        return new AggregationCount(
            searches,
            stream,
            CONDITION_ID,
            Tools.nowUTC(),
            STREAM_CREATOR,
            parameters,
            title);
    }

    protected <T extends AbstractAlertCondition> T getTestInstance(Class<T> klazz, Map<String, Object> parameters, String title) {
        try {
            return klazz.getConstructor(Searches.class, Stream.class, String.class, DateTime.class, String.class, Map.class, String.class)
                .newInstance(searches, stream, CONDITION_ID, Tools.nowUTC(), STREAM_CREATOR, parameters, title);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    protected void searchTermsShouldReturn(long count) {
        final TermsResult termsResult = mock(TermsResult.class);   
        Map<String, Long> terms = new HashMap<>();
        terms.put("user - ip1", count);
        terms.put("user - ip2", count);

        when(termsResult.getTerms()).thenReturn(terms);

		when(searches.terms(anyString(), anyList() , any(int.class), anyString(), anyString(), any(TimeRange.class), any(Direction .class))).thenReturn(termsResult);
		
    }    
    
    protected void searchResultShouldReturn() {
        final SearchResult searchResult = mock(SearchResult.class);   
        ResultMessage resultMessage = mock(ResultMessage.class);   
        List <ResultMessage> listResultMessage = Lists.newArrayList(resultMessage);

        when(searchResult.getResults()).thenReturn(listResultMessage);

		when(searches.search(anyString(), anyString(), any(TimeRange.class), any(int.class), any(int.class), any(Sorting.class))).thenReturn(searchResult);
		
    }
}
