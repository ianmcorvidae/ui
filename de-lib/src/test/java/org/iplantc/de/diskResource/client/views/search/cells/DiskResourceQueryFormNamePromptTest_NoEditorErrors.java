package org.iplantc.de.diskResource.client.views.search.cells;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.iplantc.de.client.util.SearchModelUtils;
import org.iplantc.de.diskResource.client.events.search.SubmitDiskResourceQueryEvent;

import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GxtMockitoTestRunner;
import com.google.gwtmockito.fakes.FakeSimpleBeanEditorDriverProvider;

import com.sencha.gxt.widget.core.client.event.SelectEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

@RunWith(GxtMockitoTestRunner.class)
public class DiskResourceQueryFormNamePromptTest_NoEditorErrors {

    private DiskResourceQueryFormNamePrompt namePrompt;
    @Mock SearchModelUtils searchModelUtilsMock;

    @Before public void setUp() {
        GwtMockito.useProviderForType(SimpleBeanEditorDriver.class, new FakeSimpleBeanEditorDriverProvider(false));
        namePrompt = new DiskResourceQueryFormNamePrompt();
        namePrompt.searchModelUtils = searchModelUtilsMock;
    }

    /**
     * Verify the following when {@link DiskResourceQueryFormNamePrompt#cancelSaveFilterBtn} is clicked;<br/>
     */
    @Test public void testOnCancelSaveFilter_noErrors() {
        final String originalName = "originalName";
        namePrompt.originalName = originalName;
        DiskResourceQueryFormNamePrompt spy = spy(namePrompt);
        spy.onCancelSaveFilter(mock(SelectEvent.class));

        final ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        verify(spy.name).setValue(stringCaptor.capture());
        assertEquals("Verify that the name field is reset", originalName, stringCaptor.getValue());

        // Verify that the form is hidden
        verify(spy).hide();
    }

    /**
     * Verify the following when {@link DiskResourceQueryFormNamePrompt#saveFilterBtn} is clicked;<br/>
     */
    @Test public void testOnSaveFilterSelected_noErrors() {
        DiskResourceQueryFormNamePrompt spy = spy(namePrompt);
        spy.onSaveFilterSelected(mock(SelectEvent.class));

        // Verify that the appropriate event is fired
        verify(spy).fireEvent(any(SubmitDiskResourceQueryEvent.class));

        // Verify that the form is hidden
        verify(spy).hide();
    }

}
