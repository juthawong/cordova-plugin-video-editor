/**
 * Video Editor Plugin allows the user to edit audio and video files
 * @class VideoEditor
 * @example 
 **/

/*globals require, module*/

var argscheck = require('cordova/argscheck'),
    exec = require('cordova/exec'),
    videoEditorExport = {};

/**
 * Example function
 * @param {Function} successCallback
 * @param {Function} errorCallback
 * @param {Object} options
 */
videoEditorExport.example = function (successCallback, errorCallback, options) {
    'use strict';
    argscheck.checkArgs('fFO', 'VideoEditor.example', arguments);
    options = options || {};
    var getValue = argscheck.getValue,
        args = [getValue(options.option, 'option-default-value'), getValue(options.test, 3)];
    exec(successCallback, errorCallback, "VideoEditor", "example", args);
};

module.exports = videoEditorExport;