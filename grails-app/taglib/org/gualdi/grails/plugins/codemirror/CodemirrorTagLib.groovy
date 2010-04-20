package org.gualdi.grails.plugins.codemirror

/**
 * @author Stefano Gualdi <stefano.gualdi@gmail.com>
 */

class CodemirrorTagLib {
	
	static namespace = "codemirror"
	
	def resources = { attrs ->
        def editor = new Codemirror(request, attrs)
        out << editor.renderResources()
    }
	
	def config = { attrs, body ->
        def cfg = new CodemirrorConfig(request)
		
		def var = attrs.remove('var');
        try {
			if (var) {
				def value = body()
	            cfg.addComplexConfigItem(var, value)
			}
			else {
	            cfg.addConfigItem(attrs)
			}
        }
        catch (Exception e) {
            throwTagError(e.message)
        }
    }

    def editor = { attrs, body ->
        def editor = new Codemirror(request, attrs, body())
        out << editor.renderEditor()
    }
}
