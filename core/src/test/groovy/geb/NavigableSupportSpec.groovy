/* Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb

import geb.test.util.*
import spock.lang.*

class NavigableSupportSpec extends GebSpecWithServer {

	def setupSpec() {
		server.get = { req, res ->
			res.outputStream << """
			<html>
			<body>
				<p class="a">a</p>
				<p class="b">b</p>
				<p class="c">c</p>
			</body>
			</html>"""
		}
	}
	
	def setup() {
		go()
	}
	
	def "no args"() {
		expect:
		$().tag() == "html"
	}

	def "just index"() {
		expect:
		$(0).tag() == "html"
	}

	def "just selector"() {
		expect:
		$("p").size() == 3
	}
	
	def "just attributes"() {
		expect:
		$(class: "a").text() == "a"
	}

	def "just attributes - but text"() {
		expect:
		$(text: "a").text() == "a"
	}

	def "selector and index"() {
		expect:
		$("p", 1).text() == "b"
	}

	def "selector and attributes"() {
		expect:
		$("p", class: "c").text() == "c"
	}
	
	@Ignore // See NavigableSupport
	def "attributes and index"() {
		expect:
		$(1, class: ~/\w/).text() == "b"
	}

	@Ignore // See NavigableSupport
	def "selector, attributes and index"() {
		expect:
		$("p", 1, class: ~/\w/).text() == "b"
	}
	
}