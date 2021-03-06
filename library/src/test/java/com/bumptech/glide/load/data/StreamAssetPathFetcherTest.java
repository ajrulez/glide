package com.bumptech.glide.load.data;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.res.AssetManager;

import com.bumptech.glide.Priority;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.io.InputStream;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class StreamAssetPathFetcherTest {
  @Mock AssetManager assetManager;
  @Mock InputStream expected;
  @Mock DataFetcher.DataCallback<InputStream> callback;

  private StreamAssetPathFetcher fetcher;
  private String assetPath;

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
    assetPath = "/some/asset/path";
    fetcher = new StreamAssetPathFetcher(assetManager, assetPath);
    when(assetManager.open(eq(assetPath))).thenReturn(expected);
  }

  @Test
  public void testOpensInputStreamForPathWithAssetManager() throws Exception {
    fetcher.loadData(Priority.NORMAL, callback);
    verify(callback).onDataReady(eq(expected));
  }

  @Test
  public void testClosesOpenedInputStreamOnCleanup() throws Exception {
    fetcher.loadData(Priority.NORMAL, callback);
    fetcher.cleanup();

    verify(expected).close();
  }

  @Test
  public void testDoesNothingOnCleanupIfNoDataLoaded() throws IOException {
    fetcher.cleanup();
    verify(expected, never()).close();
  }

  @Test
  public void testDoesNothingOnCancel() throws Exception {
    fetcher.loadData(Priority.NORMAL, callback);
    fetcher.cancel();
    verify(expected, never()).close();
  }
}