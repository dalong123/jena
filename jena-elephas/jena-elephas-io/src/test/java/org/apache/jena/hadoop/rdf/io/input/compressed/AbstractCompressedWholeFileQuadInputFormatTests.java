/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jena.hadoop.rdf.io.input.compressed;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.jena.hadoop.rdf.io.HadoopIOConstants;
import org.apache.jena.hadoop.rdf.io.input.AbstractNodeTupleInputFormatTests;
import org.apache.jena.hadoop.rdf.types.QuadWritable;
import org.apache.jena.query.Dataset ;
import org.apache.jena.query.DatasetFactory ;
import org.apache.jena.rdf.model.Model ;
import org.apache.jena.rdf.model.ModelFactory ;
import org.apache.jena.rdf.model.Property ;
import org.apache.jena.rdf.model.Resource ;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFWriterRegistry;
import org.apache.jena.sparql.core.Quad ;

/**
 * Abstract tests for compressed whole file quad formats
 * 
 * 
 */
public abstract class AbstractCompressedWholeFileQuadInputFormatTests extends
        AbstractNodeTupleInputFormatTests<Quad, QuadWritable> {
    
    private static final Charset utf8 = Charset.forName("utf-8");

    @Override
    protected Configuration prepareConfiguration() {
        Configuration config = super.prepareConfiguration();
        config.set(HadoopIOConstants.IO_COMPRESSION_CODECS, this.getCompressionCodec().getClass().getCanonicalName());
        return config;
    }

    @Override
    protected OutputStream getOutputStream(File f) throws IOException {
        CompressionCodec codec = this.getCompressionCodec();
        if (codec instanceof Configurable) {
            ((Configurable) codec).setConf(this.prepareConfiguration());
        }
        FileOutputStream fileOutput = new FileOutputStream(f, false);
        return codec.createOutputStream(fileOutput);
    }

    /**
     * Gets the compression codec to use
     * 
     * @return Compression codec
     */
    protected abstract CompressionCodec getCompressionCodec();

    /**
     * Indicates whether inputs can be split, defaults to false for compressed
     * input tests
     */
    @Override
    protected boolean canSplitInputs() {
        return false;
    }

    private void writeTuples(Dataset ds, OutputStream output) {
        RDFDataMgr.write(output, ds, RDFWriterRegistry.defaultSerialization(this.getRdfLanguage()));
    }

    /**
     * Gets the RDF language to write out generated tuples in
     * 
     * @return RDF language
     */
    protected abstract Lang getRdfLanguage();

    private void writeGoodTuples(OutputStream output, int num) throws IOException {
        Dataset ds = DatasetFactory.createMem();
        Model m = ModelFactory.createDefaultModel();
        Resource currSubj = m.createResource("http://example.org/subjects/0");
        Property predicate = m.createProperty("http://example.org/predicate");
        for (int i = 0; i < num; i++) {
            if (i % 100 == 0) {
                ds.addNamedModel("http://example.org/graphs/" + (i / 100), m);
                m = ModelFactory.createDefaultModel();
            }
            if (i % 10 == 0) {
                currSubj = m.createResource("http://example.org/subjects/" + (i / 10));
            }
            m.add(currSubj, predicate, m.createTypedLiteral(i));
        }
        if (!m.isEmpty()) {
            ds.addNamedModel("http://example.org/graphs/extra", m);
        }
        this.writeTuples(ds, output);
    }

    @Override
    protected final void generateTuples(OutputStream output, int num) throws IOException {
        this.writeGoodTuples(output, num);
        output.close();
    }

    @Override
    protected final void generateMixedTuples(OutputStream output, int num) throws IOException {
        // Write good data
        this.writeGoodTuples(output, num / 2);

        // Write junk data
        byte[] junk = "junk data\n".getBytes(utf8);
        for (int i = 0; i < num / 2; i++) {
            output.write(junk);
        }

        output.flush();
        output.close();
    }

    @Override
    protected final void generateBadTuples(OutputStream output, int num) throws IOException {
        byte[] junk = "junk data\n".getBytes(utf8);
        for (int i = 0; i < num; i++) {
            output.write(junk);
        }
        output.flush();
        output.close();
    }
}
