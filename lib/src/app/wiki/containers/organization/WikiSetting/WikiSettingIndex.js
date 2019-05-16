'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _react = require('react');

var _react2 = _interopRequireDefault(_react);

var _reactRouterDom = require('react-router-dom');

var _boot = require('@choerodon/boot');

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

var WikiSettingHome = (0, _boot.asyncRouter)(function () {
  return import('./WikiSettingHome');
});

var WikiSettingIndex = function WikiSettingIndex(_ref) {
  var match = _ref.match;
  return _react2['default'].createElement(
    _reactRouterDom.Switch,
    null,
    _react2['default'].createElement(_reactRouterDom.Route, { exact: true, path: match.url, component: WikiSettingHome }),
    _react2['default'].createElement(_reactRouterDom.Route, { path: '*', component: _boot.nomatch })
  );
};

exports['default'] = WikiSettingIndex;