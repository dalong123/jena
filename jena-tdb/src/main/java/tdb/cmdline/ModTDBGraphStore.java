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

package tdb.cmdline;

import org.apache.jena.update.GraphStore ;
import org.apache.jena.update.GraphStoreFactory ;
import arq.cmdline.CmdArgModule ;
import arq.cmdline.CmdGeneral ;
import arq.cmdline.ModGraphStore ;

public class ModTDBGraphStore extends ModGraphStore
{
    private ModTDBDataset modTDBDataset = new ModTDBDataset() ;
    
    public ModTDBGraphStore() { super() ; }
    
    @Override
    public void registerWith(CmdGeneral cmdLine)
    {
        modTDBDataset.registerWith(cmdLine) ;
    }

    @Override
    public void processArgs(CmdArgModule cmdLine)
    {
        modTDBDataset.processArgs(cmdLine) ;
    }
    
    @Override
    public GraphStore createGraphStore()
    {
        // Simply return the DatasetGraphTDB
        return GraphStoreFactory.create(modTDBDataset.createDataset()) ;
    }
}
