/**
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

package org.openjena.riot;

import java.io.InputStream ;
import java.io.OutputStream ;
import java.util.Iterator ;

import org.openjena.atlas.data.SerializationFactory ;
import org.openjena.atlas.lib.Sink ;
import org.openjena.riot.lang.LabelToNode ;
import org.openjena.riot.lang.LangNQuads ;
import org.openjena.riot.lang.LangNTriples ;
import org.openjena.riot.out.NodeToLabel ;
import org.openjena.riot.out.SinkQuadOutput ;
import org.openjena.riot.out.SinkTripleOutput ;
import org.openjena.riot.system.IRIResolver ;
import org.openjena.riot.system.ParserProfileBase ;
import org.openjena.riot.system.Prologue ;
import org.openjena.riot.tokens.Tokenizer ;
import org.openjena.riot.tokens.TokenizerFactory ;

import com.hp.hpl.jena.graph.Triple ;
import com.hp.hpl.jena.sparql.core.Quad ;
import com.hp.hpl.jena.sparql.engine.binding.Binding ;
import com.hp.hpl.jena.sparql.engine.binding.BindingInputStream ;
import com.hp.hpl.jena.sparql.engine.binding.BindingOutputStream ;

public class SerializationFactoryFinder
{
    public static SerializationFactory<Binding> bindingSerializationFactory()
    {
        return new SerializationFactory<Binding>()
        {
            public Sink<Binding> createSerializer(OutputStream out)
            {
                return new BindingOutputStream(out);
            }
            
            public Iterator<Binding> createDeserializer(InputStream in)
            {
                return new BindingInputStream(in);
            }

            public long getEstimatedMemorySize(Binding item)
            {
                // TODO traverse the binding, and add up the variable + node sizes + object overhead
                return 0 ;
            }
        };
    }
    
    public static SerializationFactory<Triple> tripleSerializationFactory()
    {
        return new SerializationFactory<Triple>()
        {
            public Sink<Triple> createSerializer(OutputStream out)
            {
                return new SinkTripleOutput(out, null, NodeToLabel.createBNodeByLabelEncoded()) ;
            }
            
            public Iterator<Triple> createDeserializer(InputStream in)
            {
                Tokenizer tokenizer = TokenizerFactory.makeTokenizerASCII(in) ;
                ParserProfileBase profile = new ParserProfileBase(new Prologue(null, IRIResolver.createNoResolve()), null, LabelToNode.createUseLabelEncoded()) ;
                LangNTriples parser = new LangNTriples(tokenizer, profile, null) ;
                return parser ;
            }
            
            public long getEstimatedMemorySize(Triple item)
            {
                // TODO
                return 0 ;
            }
        };
    }
    
    public static SerializationFactory<Quad> quadSerializationFactory()
    {
        return new SerializationFactory<Quad>()
        {
            public Sink<Quad> createSerializer(OutputStream out)
            {
                return new SinkQuadOutput(out, null, NodeToLabel.createBNodeByLabelEncoded()) ;
            }
            
            public Iterator<Quad> createDeserializer(InputStream in)
            {
                Tokenizer tokenizer = TokenizerFactory.makeTokenizerASCII(in) ;
                ParserProfileBase profile = new ParserProfileBase(new Prologue(null, IRIResolver.createNoResolve()), null, LabelToNode.createUseLabelEncoded()) ;
                LangNQuads parser = new LangNQuads(tokenizer, profile, null) ;
                return parser ;
            }
            
            public long getEstimatedMemorySize(Quad item)
            {
                // TODO
                return 0 ;
            }
        };
    }
}
