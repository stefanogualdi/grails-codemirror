package org.gualdi.grails.plugins.codemirror

import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.gualdi.grails.plugins.codemirror.utils.PluginUtils

/**
 * @author Stefano Gualdi <stefano.gualdi@gmail.com>
 */

class CodemirrorConfig {
	private final Logger log = Logger.getLogger(getClass())

    static final REQUEST_CONFIG = "codemirror.plugin.config"
	
	static final PLUGIN_NAME = "codemirror"
	
	static final DEFAULT_INSTANCENAME = "codeeditor"
	
	static final JS_PATH = "/js/codemirror/"
	static final CSS_PATH = "/css/codemirror/"
	
	static final EDITOR_TYPES = [
		'js': [parserfile: ['tokenizejavascript.js', 'parsejavascript.js'], stylesheet: 'jscolors.css'],
		'xml': [parserfile: 'parsexml.js', stylesheet: 'xmlcolors.css'],
		'css': [parserfile: 'parsecss.js', stylesheet: 'csscolors.css'], 
		'html': [parserfile: ['parsexml.js', 'parsehtmlmixed.js'], stylesheet: 'xmlcolors.css']
	]
	
	def contextPath
    def basePath

    def instanceName

	def config
	def localConfig
	
	CodemirrorConfig(request, attrs = null) {
		this.contextPath = request.contextPath
		this.basePath = PluginUtils.getPluginResourcePath(this.contextPath, this.PLUGIN_NAME)

		this.localConfig = [:]
		
		createOrRetrieveConfig(request)

		checkPathConfig()
		checkTypesConfig(attrs)

		if (attrs) {
	        this.instanceName = attrs.remove("name") ?: this.DEFAULT_INSTANCENAME

			addConfigItem(attrs, true)			
		}
    }

	private createOrRetrieveConfig(request) {
        if (!request[REQUEST_CONFIG]) {
            request[REQUEST_CONFIG] = [:]
        }
        this.config = request[REQUEST_CONFIG]
	}
	
	private checkPathConfig() {
		if (!localConfig['path']) {
			addConfigItem([path: this.basePath + this.JS_PATH], true)
		}
	}

	private checkTypesConfig(attrs) {
		// You can do it better!!!!!

		def parsers = []
		def stylesheets = []
        attrs?.each { key, value ->
			if (key in EDITOR_TYPES.keySet() && value == "true") {
				// TODO: Create helper which take a closure to handle path adding 
				if (EDITOR_TYPES[key]['parserfile'] instanceof ArrayList) {
					EDITOR_TYPES[key]['parserfile'].each { v ->
						if (!(v in parsers)) {
							parsers << v
						}
					}
				}
				else {
					if (!(EDITOR_TYPES[key]['parserfile'] in parsers)) {
						parsers << EDITOR_TYPES[key]['parserfile']
					}
				}
				if (!(EDITOR_TYPES[key]['stylesheet'] in stylesheets)) {
					stylesheets << EDITOR_TYPES[key]['stylesheet']
				}
			}
		}
		
		// Ugly!!!
		EDITOR_TYPES.keySet().each { k ->
			attrs.remove(k)
		}
		
		if (parsers) {
			def tmp = parsers.collect { p -> "'${p}'" }
			addComplexConfigItem('parserfile', "[${tmp.join(',')}]")
		}
		if (stylesheets) {
			def tmp = stylesheets.collect { s -> "'${this.basePath + this.CSS_PATH}${s}'" }
			addComplexConfigItem('stylesheet', "[${tmp.join(',')}]")
		}
	}

    def addConfigItem(attrs, local = false) {
        attrs?.each { key, value ->
			def tmp = value?.trim()
			if (!tmp?.isNumber() && !tmp?.equalsIgnoreCase('true') && !tmp?.equalsIgnoreCase('false')) {
				tmp = "'${tmp}'"
			}
			
			if (local) {
				this.localConfig[key] = tmp
			}
			else {
               	this.config[key] = tmp
			}
        }
    }

	def addComplexConfigItem(var, value) {
		this.config[var] = value
		/*
        if (var in ALLOWED_CONFIG_ITEMS || var.startsWith('toolbar_')) {
			this.config[var] = value
		}
		else {
		    throw new UnknownOptionException("Unknown option: ${var}. Option names are case sensitive! Check the spelling.")
        }
		*/
    }

    def getConfiguration() {
		def configs = []
		this.config.each {k, v ->
			if (!localConfig[k]) {
				configs << "${k}: ${v}"
			}
		}
		this.localConfig.each {k, v ->
			configs << "${k}: ${v}"
		}

		StringBuffer configuration = new StringBuffer()
        if (configs.size()) {
            configuration << """, {\n"""
           	configuration << configs.join(",\n")
            configuration << """}\n"""
        }
	
        return configuration
    }

}