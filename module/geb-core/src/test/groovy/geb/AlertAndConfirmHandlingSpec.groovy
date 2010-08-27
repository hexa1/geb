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

/*@Ignore*/
class AlertAndConfirmHandlingSpec extends GebSpecWithServer {
	
	def setupSpec() {
		server.get = { req, res ->
			res.outputStream << """
				<html>
				<body>
					<script type="text/javascript" charset="utf-8">
						var i = 0;
						var confirmResult;
					</script>
					<input type="button" name="hasAlert" onclick="alert(++i);" />
					<input type="button" name="noAlert" />
					<input type="button" name="hasConfirm" onclick="confirmResult = confirm(++i);" />
					<input type="button" name="noConfirm" />
				</body>
				</html>
			"""
		}
	}
		
	def setup() {
		go()
	}
	
	// HTMLUnit does something strange when converting types
	// and changes integer '1' to string '1.0' when coercing
	private rationalise(value) {
		value[0].toInteger()
	}
	
	def "handle alert"() {
		expect:
		rationalise(withAlert { hasAlert().click() }) == 1
	}
	
	def "expect alert but don't get it"() {
		when:
		withAlert { noAlert().click() }
		then:
		thrown(AssertionError)
	}
	
	def "no alert and don't get one"() {
		when:
		withNoAlert { noAlert().click() }
		then:
		notThrown(AssertionError)
	}

	def "no alert and do get one"() {
		when:
		withNoAlert { hasAlert().click() }
		then:
		thrown(AssertionError)
	}
	
	def "nested alerts"() {
		when:
		def innerMsg
		def outerMsg = withAlert { 
			innerMsg = withAlert { hasAlert().click() }
			hasAlert().click()
		}
		then:
		rationalise(innerMsg) == 1
		rationalise(outerMsg) == 2
	}
	
	private getConfirmResult() {
		js.confirmResult
	}
	
	def "handle confirm"() {
		expect:
		rationalise(withConfirm(true) { hasConfirm().click() }) == 1
		confirmResult == true
		rationalise(withConfirm(false) { hasConfirm().click() }) == 2
		confirmResult == false
		rationalise(withConfirm { hasConfirm().click() }) == 3
		confirmResult == true
	}
	
	def "expect confirm but don't get it"() {
		when:
		withConfirm { noConfirm().click() }
		then:
		thrown(AssertionError)
	}
	
	def "no confirm and don't get one"() {
		when:
		withNoAlert { noConfirm().click() }
		then:
		notThrown(AssertionError)
	}

	def "no confirm and do get one"() {
		when:
		withNoConfirm { hasConfirm().click() }
		then:
		thrown(AssertionError)
	}
	
	def "nested confirms"() {
		when:
		def innerMsg
		def innerConfirmResult
		def outerConfirmResult
		def outerMsg = withConfirm(true) { 
			innerMsg = withConfirm(false) { hasConfirm().click() }
			innerConfirmResult = confirmResult
			hasConfirm().click()
		}
		outerConfirmResult = confirmResult
		then:
		rationalise(innerMsg) == 1
		rationalise(outerMsg) == 2
		innerConfirmResult == false
		outerConfirmResult == true
	}
	
	def "pages and modules have the methods too"() {
		given:
		page AlertAndConfirmHandlingSpecPage
		when:
		page.testOneOfTheMethods()
		mod.testOneOfTheMethods()
		then:
		notThrown(Exception)
	}
	
}

class AlertAndConfirmHandlingSpecPage extends Page {
	static content = {
		mod { module AlertAndConfirmHandlingSpecModule }
	}
	
	def testOneOfTheMethods() {
		withNoAlert { true }
	}
}

class AlertAndConfirmHandlingSpecModule extends Module {
	def testOneOfTheMethods() {
		withNoAlert { true }
	}
}