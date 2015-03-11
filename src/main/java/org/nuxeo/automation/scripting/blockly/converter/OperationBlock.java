/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     <a href="mailto:tdelprat@nuxeo.com">Tiry</a>
 */
package org.nuxeo.automation.scripting.blockly.converter;

import org.dom4j.Element;
import org.nuxeo.automation.scripting.blockly.BlocklyOperationWrapper;

/**
 *
 * @author <a href="mailto:tdelprat@nuxeo.com">Tiry</a>
 * @since TODO
 */
public class OperationBlock {

    protected final Element block;

    protected final BlocklyOperationWrapper wrapper;

    protected Element input;

    public OperationBlock(Element block, BlocklyOperationWrapper wrapper) {
        this.block=block;
        this.wrapper=wrapper;
    }

    public Element getInput() {
        return input;
    }

    public void setInput(Element input) {
        this.input = input;
    }

    public Element getBlock() {
        return block;
    }

    public BlocklyOperationWrapper getWrapper() {
        return wrapper;
    }



}
