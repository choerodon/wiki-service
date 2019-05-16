'use strict';

Object.defineProperty(exports, "__esModule", {
    value: true
});

var _classCallCheck2 = require('babel-runtime/helpers/classCallCheck');

var _classCallCheck3 = _interopRequireDefault(_classCallCheck2);

var _createClass2 = require('babel-runtime/helpers/createClass');

var _createClass3 = _interopRequireDefault(_createClass2);

var _possibleConstructorReturn2 = require('babel-runtime/helpers/possibleConstructorReturn');

var _possibleConstructorReturn3 = _interopRequireDefault(_possibleConstructorReturn2);

var _inherits2 = require('babel-runtime/helpers/inherits');

var _inherits3 = _interopRequireDefault(_inherits2);

var _dec, _class;

var _react = require('react');

var _react2 = _interopRequireDefault(_react);

var _reactRouterDom = require('react-router-dom');

var _mobxReact = require('mobx-react');

var _boot = require('@choerodon/boot');

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

var OrganizationWikiSetting = (0, _boot.asyncRouter)(function () {
    return import('./organization/WikiSetting');
});
var ProjectWikiSetting = (0, _boot.asyncRouter)(function () {
    return import('./project/WikiSetting');
});

var WIKIIndex = (_dec = (0, _mobxReact.inject)('AppState'), _dec(_class = function (_React$Component) {
    (0, _inherits3['default'])(WIKIIndex, _React$Component);

    function WIKIIndex() {
        (0, _classCallCheck3['default'])(this, WIKIIndex);
        return (0, _possibleConstructorReturn3['default'])(this, (WIKIIndex.__proto__ || Object.getPrototypeOf(WIKIIndex)).apply(this, arguments));
    }

    (0, _createClass3['default'])(WIKIIndex, [{
        key: 'render',
        value: function render() {
            var _props = this.props,
                match = _props.match,
                AppState = _props.AppState;

            var langauge = AppState.currentLanguage;
            var IntlProviderAsync = (0, _boot.asyncLocaleProvider)(langauge, function () {
                return import('../locale/' + langauge);
            });
            return _react2['default'].createElement(
                IntlProviderAsync,
                null,
                _react2['default'].createElement(
                    _reactRouterDom.Switch,
                    null,
                    _react2['default'].createElement(_reactRouterDom.Route, { path: match.url + '/organization/space', component: OrganizationWikiSetting }),
                    _react2['default'].createElement(_reactRouterDom.Route, { path: match.url + '/project/space', component: ProjectWikiSetting }),
                    _react2['default'].createElement(_reactRouterDom.Route, { path: '*', component: _boot.nomatch })
                )
            );
        }
    }]);
    return WIKIIndex;
}(_react2['default'].Component)) || _class);
exports['default'] = WIKIIndex;