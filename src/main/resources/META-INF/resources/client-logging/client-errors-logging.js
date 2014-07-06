(function () {

	/* ---------------------------------------------------------------
	 * Some helper functions here
	 * --------------------------------------------------------------- */
	var isIE = function(){ // usage: if (isIE() && isIE()<9)
		var myNav = navigator.userAgent.toLowerCase();
		return (myNav.indexOf('msie') != -1) ? parseInt(myNav.split('msie')[1]) : false;
	};

	/* ---------------------------------------------------------------
	 * Set up console if browser doesnt support it
	 * (taken from twitter's source code)
	 * ---------------------------------------------------------------
	 */
	var noop = function () {};
	var methods = [
		'assert', 'clear', 'count', 'debug', 'dir', 'dirxml', 'error',
		'exception', 'group', 'groupCollapsed', 'groupEnd', 'info', 'log',
		'markTimeline', 'profile', 'profileEnd', 'table', 'time', 'timeEnd',
		'timeStamp', 'trace', 'warn'
	];
	var length = methods.length;
	var console = (window.console = window.console || {});

	var method;
	while (length--) {
		method = methods[length];

		// Only stub undefined methods.
		if (!console[method]) {
			console[method] = noop;
		}
	}

	/* ---------------------------------------------------------------
	 * In DEV mode, show IE8- console logs on the page.
	 * ---------------------------------------------------------------
	 */
	var devMode = false;
	if (devMode && isIE() && isIE()<9){
		console['log'] = function(message){
			var errorbox = document.createElement('div');
			errorbox.className = 'fancyerror';
			errorbox.innerHTML = 'JS: <span class="errmsg" >' + message.replace('<', '&lt;').replace('>', '&gt;') + '</span>';
			document.body.appendChild(errorbox);
		}
	}


	/* ---------------------------------------------------------------
	 * Fake JSON for when JSON is not supported. Some code is taken from json2.js
	 * We only need to fully supports strings and numbers, cause that's all we need for logging js errors.
	 * ---------------------------------------------------------------
	 */
	var escapable = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g;
	var meta = {    // table of character substitutions
		'\b': '\\b',
		'\t': '\\t',
		'\n': '\\n',
		'\f': '\\f',
		'\r': '\\r',
		'"' : '\\"',
		'\\': '\\\\'
	};

	function quoteValue(value) {
		if (value == undefined)
			return 'null';

		if (typeof (value) == 'number')
			return isFinite(value) ? String(value) : 'null';

		escapable.lastIndex = 0;
		return escapable.test(value) ? '"' + value.replace(escapable, function (a) {
			var c = meta[a];
			return typeof c === 'string' ? c : '\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
		}) + '"' : '"' + value + '"';
	}

	var JSON = JSON || {}; // do not override global JSON object, since someone may bring json2 later
	JSON.stringify = JSON.stringify || function(obj) {
		var t = typeof (obj);
		if (t != "object" || obj === null) { // simple data type
			if (t == "string" || t == 'number')
				obj = quoteValue(obj);
			return String(obj);
		} else {
			// recurse array or object
			var n, v, json = [], arr = (obj && obj.constructor == Array);
			for (n in obj) {
				v = obj[n];
				t = typeof (v);
				if (t == "string"  || t == 'number')
					v = quoteValue(v);
				else if (t == "object" && v !== null)
					v = JSON.stringify(v);
				json.push((arr ? "" : '"' + n + '":') + String(v));
			}
			return (arr ? "[" : "{") + String(json) + (arr ? "]" : "}");
		}
	};


	/* ---------------------------------------------------------------
	 * Actual code start here
	 * ---------------------------------------------------------------
	 */
	var consoleLogError = function(message, file, line, column, errorObj) {
		var msg = message;
		if (file || line || column) {
			msg += "\nat: " + file + ":" + line + ":" + column;
		}
		if (errorObj !== undefined) //so it won't blow up in the rest of the browsers
			msg += '\nstacktrace:\n' + errorObj.stack;

		console.log(msg);

		return false;
	};

	/*
	 * Sends error to server using native XMLHttpRequest (for when jquery is not available)
	 */
	var sendErrorNative = function(url, body){
		if (window.XMLHttpRequest) {
			var xhr = new XMLHttpRequest();
			xhr.open('POST', url);
			xhr.setRequestHeader('Content-Type', 'text/plain;charset=UTF-8');

			xhr.send(body);
			xhr.onreadystatechange=function(){
				if (xhr.readyState==4 && xhr.status==404){
					unableToReachEndpointErrorsCount ++;
				}
			}
		}
		return true;
	};

	var sendError = function(url, body){
		$.post(url, body).error(function(obj){
			if (obj.status == 404){
				unableToReachEndpointErrorsCount ++;
			}
		});
	};

	var generateErrorString = function(message, file, line, column, errorObj){
		var stack = null;
		if (errorObj) stack = errorObj.stack;
		return (window.JSON || JSON).stringify({message: message, file:file, line:line?line:0, column:column?column:0, errorObj:stack});
	};


	var defaultscripturl = '/clienterrorlogger/log';
	var unableToReachEndpointErrorsCount = 0;
	var scripturl = null;

	var getUrl = function(){
		if (scripturl != null){
			return scripturl;
		}

		// keep trying
		if (window.UOA && window.UOA.logging) {
			scripturl = window.UOA.logging.url;
			return scripturl;
		} else {
			return defaultscripturl;
		}

	};

	window.onerror = function (message, file, line, column, errorObj){
		if (devMode) consoleLogError(message, file, line, column, errorObj);

		// check if we are able to reach server using current url
		var url = getUrl();
		if (url == defaultscripturl && unableToReachEndpointErrorsCount>5){
			return;
		}

		var body = generateErrorString(message, file, line, column, errorObj);
		try{
			if (typeof jQuery == 'undefined'){
				sendErrorNative(url, body);
			}else{
				sendError(url, body);
			}
		}catch (e){
			console.log(e);
			unableToReachEndpointErrorsCount++;
		}
	};

})();