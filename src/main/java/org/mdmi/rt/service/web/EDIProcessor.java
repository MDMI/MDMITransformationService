/*-
 * ========================LICENSE_START=================================
 * Smooks Example :: EDI-to-XML
 * %%
 * Copyright (C) 2020 Smooks
 * %%
 * Licensed under the terms of the Apache License Version 2.0, or
 * the GNU Lesser General Public License version 3.0 or later.
 *
 * SPDX-License-Identifier: Apache-2.0 OR LGPL-3.0-or-later
 *
 * ======================================================================
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ======================================================================
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * =========================LICENSE_END==================================
 */
package org.mdmi.rt.service.web;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.smooks.Smooks;
import org.smooks.api.ExecutionContext;
import org.smooks.engine.DefaultApplicationContextBuilder;
import org.smooks.io.sink.WriterSink;
import org.smooks.support.StreamUtils;

/**
 * Simple example main class.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class EDIProcessor {

	Smooks unsmooks = null;

	Smooks smooks = null;

	// private static byte[] messageIn = readInputMessage();

	protected String transformEDI2XML(byte[] messageIn) throws Exception {
		// Instantiate Smooks with the config...
		if (smooks == null) {
			smooks = new Smooks(
				new DefaultApplicationContextBuilder().withClassLoader(EDIProcessor.class.getClassLoader()).build());
			smooks.addResourceConfigs("asmile-smooks-parser-config.xml");
		}
		try {
			// Create an exec context - no profiles....
			ExecutionContext executionContext = smooks.createExecutionContext();
			CharArrayWriter writer = new CharArrayWriter();
			org.smooks.api.io.Source aaa = new org.smooks.io.source.StreamSource(new ByteArrayInputStream(messageIn));
			smooks.filterSource(executionContext, aaa, new WriterSink(writer));
			return writer.toString();
		} finally {
			smooks.close();
		}
	}

	public String transformXML2EDI(byte[] messageIn) throws Exception {
		// Instantiate Smooks with the config...
		if (unsmooks == null) {
			unsmooks = new Smooks(
				new DefaultApplicationContextBuilder().withClassLoader(EDIProcessor.class.getClassLoader()).build());
			smooks.addResourceConfigs("smile-smooks-unparser-config.xml");
		}

		try {
			// Create an exec context - no profiles....
			ExecutionContext executionContext = smooks.createExecutionContext();
			CharArrayWriter writer = new CharArrayWriter();
			org.smooks.api.io.Source aaa = new org.smooks.io.source.StreamSource(new ByteArrayInputStream(messageIn));
			unsmooks.filterSource(executionContext, aaa, new WriterSink(writer));
			return writer.toString();

		} finally {
			smooks.close();
		}
	}

	public static void main(String[] args) throws Exception {

		byte[] messageIn = readInputMessage();
		System.out.println("\n\n==============Message In==============");
		System.out.println(new String(messageIn));
		System.out.println("======================================\n");

		pause("The EDI input stream can be seen above.  Press 'enter' to see this stream transformed into XML...");
		EDIProcessor edi = new EDIProcessor();
		String messageOut = edi.transformEDI2XML(messageIn);

		System.out.println("==============Message Out=============");
		System.out.println(messageOut);
		System.out.println("======================================\n\n");

		pause("And that's it!  Press 'enter' to finish...");
	}

	private static byte[] readInputMessage() {
		try {
			return StreamUtils.readStream(new FileInputStream("input-message.edi"));
		} catch (IOException e) {
			e.printStackTrace();
			return "<no-message/>".getBytes();
		}
	}

	private static void pause(String message) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("> " + message);
			in.readLine();
		} catch (IOException e) {
		}
		System.out.println("\n");
	}
}
