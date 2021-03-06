/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb.content

import geb.Page
import geb.error.InvalidPageContent

class PageContentTemplateParams {

    /**
     * The value of the 'required' option, as a boolean according to the Groovy Truth. Defaults to true.
     */
    final boolean required

    /**
     * The value of the 'cache' option, as a boolean according to the Groovy Truth. Defaults to false.
     */
    final boolean cache

    /**
     * If the to option was a list, this will be the specified list. Defaults to null.
     */
    List toList

    /**
     * If the to option was a single page class or instance, this will be the value used. Defaults to null.
     */
    def toSingle

    /**
     * The value of the 'page' option. Defaults to null.
     */
    Class<? extends Page> page

    /**
     * The value of the 'wait' option. Defaults to null (no wait).
     */
    final wait

    /**
     * The value of the 'toWait' options. Defaults to null (no waiting when switching pages).
     */
    final toWait

    Map all = [:]
    String code
    String label
    Boolean isUsernameField
    Boolean isPasswordField
    Boolean isBrokerCodeField
    def codeModeFieldType
    PageContentTemplate pageContentTemplate

    PageContentTemplateParams(PageContentTemplate owner, Map<String, ?> params) {
        def paramsToProcess = params == null ? Collections.emptyMap() : new HashMap<String, Object>(params)
        paramsToProcess?.each { k, v ->
            all[k] = v
        }

        required = toBoolean(paramsToProcess, 'required', true)
        cache = toBoolean(paramsToProcess, 'cache', false)

        def toParam = paramsToProcess.remove("to")
        if (!toParam) {
            toSingle = null
            toList = null
        } else if ((toParam instanceof Class && Page.isAssignableFrom(toParam)) || toParam instanceof Page) {
            toSingle = toParam
            toList = null
        } else if (toParam instanceof List) {
            toSingle = null
            toList = toParam
        } else {
            throw new InvalidPageContent("'to' content parameter should be a class that extends Page or a list of classes that extend Page, but it isn't for $owner: $toParam")
        }

        def pageParam = paramsToProcess.remove("page")
        if (pageParam && (!(pageParam instanceof Class) || !Page.isAssignableFrom(pageParam))) {
            throw new InvalidPageContent("'page' content parameter should be a class that extends Page but it isn't for $owner: $pageParam")
        }
        page = pageParam as Class<? extends Page>

        wait   = paramsToProcess.remove ("wait")
        toWait = paramsToProcess.remove ("toWait")
        code   = paramsToProcess.remove ("code")
        label  = paramsToProcess.remove ("label")
        isUsernameField = paramsToProcess.remove ("isUsernameField") ?: false
        isPasswordField = paramsToProcess.remove ("isPasswordField") ?: false
        isBrokerCodeField = paramsToProcess.remove ("isBrokerCodeField") ?: false
        codeModeFieldType = paramsToProcess.remove("codeModeFieldType") ?: 0
        this.pageContentTemplate = owner // owner has the 'Closure factory' property, it gives an ability to refresh a page element, if the element was removed from the page and restored

        //def unrecognizedParams = paramsToProcess.keySet() as TreeSet
        // We should allow any arbitrary parameters
        //if (unrecognizedParams) {
        //    throw new InvalidPageContent("${owner.toString().capitalize()} uses unknown content parameters: ${unrecognizedParams.join(", ")}")
        //}
    }

    private static boolean toBoolean(Map<String, ?> params, String key, boolean defaultValue) {
        params.containsKey(key) ? params.remove(key) : defaultValue as boolean
    }

}
