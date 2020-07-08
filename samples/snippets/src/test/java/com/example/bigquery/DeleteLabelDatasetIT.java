/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bigquery;

import static com.google.common.truth.Truth.assertThat;
import static junit.framework.TestCase.assertNotNull;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.DatasetInfo;
import com.google.common.collect.ImmutableMap;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DeleteLabelDatasetIT {

  private String datasetName;
  private ByteArrayOutputStream bout;
  private PrintStream out;

  private static final String PROJECT_ID = requireEnvVar("GOOGLE_CLOUD_PROJECT");

  private static String requireEnvVar(String varName) {
    String value = System.getenv(varName);
    assertNotNull(
        "Environment variable " + varName + " is required to perform these tests.",
        System.getenv(varName));
    return value;
  }

  @BeforeClass
  public static void checkRequirements() {
    requireEnvVar("GOOGLE_CLOUD_PROJECT");
  }

  @Before
  public void setUp() {
    // create a temporary dataset
    datasetName = "MY_DATASET_TEST_" + UUID.randomUUID().toString().substring(0, 8);
    Map<String, String> labels = ImmutableMap.of("color", "green");
    BigQuery bigQuery = BigQueryOptions.getDefaultInstance().getService();
    DatasetInfo datasetInfo = DatasetInfo.newBuilder(datasetName).setLabels(labels).build();
    bigQuery.create(datasetInfo);

    bout = new ByteArrayOutputStream();
    out = new PrintStream(bout);
    System.setOut(out);
  }

  @After
  public void tearDown() {
    // delete a temporary dataset
    DeleteDataset.deleteDataset(PROJECT_ID, datasetName);
    System.setOut(null);
  }

  @Test
  public void testDeleteLabelDataset() {
    DeleteLabelDataset.deleteLabelDataset(datasetName);
    assertThat(bout.toString()).contains("Dataset label deleted successfully");
  }
}
