package org.gualdi.grails.plugins.codemirror

import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.gualdi.grails.plugins.codemirror.utils.PluginUtils

/**
 * @author Stefano Gualdi <stefano.gualdi@gmail.com>
 */

class Codemirror {

    private final Logger log = Logger.getLogger(getClass())

	static final PLUGIN_NAME = "codemirror"

	def config
	def initialValue

    Codemirror(request, attrs, value = "") {
		this.config = new CodemirrorConfig(request, attrs)
		this.initialValue = value
    }

    def renderResources() {
        StringBuffer outb = new StringBuffer()

        outb << """<script type="text/javascript" src="${this.config.basePath}${this.config.JS_PATH}codemirror.js"></script>"""

        return outb.toString()
    }

    def renderEditor() {
        StringBuffer outb = new StringBuffer()

        outb << """<textarea id="${this.config.instanceName}" name="${this.config.instanceName}">${this.initialValue?.encodeAsHTML()}</textarea>\n"""
        outb << """<script type="text/javascript">\n"""
        outb << """var ${this.config.instanceName} = CodeMirror.fromTextArea('${this.config.instanceName}' """

		outb << this.config.configuration

        outb << """);\n"""
        outb << """</script>\n"""

        return outb.toString()
    }
}