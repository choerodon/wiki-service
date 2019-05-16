'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports['default'] = undefined;

var _spin = require('choerodon-ui/lib/spin');

var _spin2 = _interopRequireDefault(_spin);

var _tooltip = require('choerodon-ui/lib/tooltip');

var _tooltip2 = _interopRequireDefault(_tooltip);

var _icon = require('choerodon-ui/lib/icon');

var _icon2 = _interopRequireDefault(_icon);

var _classCallCheck2 = require('babel-runtime/helpers/classCallCheck');

var _classCallCheck3 = _interopRequireDefault(_classCallCheck2);

var _createClass2 = require('babel-runtime/helpers/createClass');

var _createClass3 = _interopRequireDefault(_createClass2);

var _possibleConstructorReturn2 = require('babel-runtime/helpers/possibleConstructorReturn');

var _possibleConstructorReturn3 = _interopRequireDefault(_possibleConstructorReturn2);

var _inherits2 = require('babel-runtime/helpers/inherits');

var _inherits3 = _interopRequireDefault(_inherits2);

require('choerodon-ui/lib/spin/style');

require('choerodon-ui/lib/tooltip/style');

require('choerodon-ui/lib/icon/style');

var _react = require('react');

var _react2 = _interopRequireDefault(_react);

var _reactRouterDom = require('react-router-dom');

var _reactIntl = require('react-intl');

var _boot = require('@choerodon/boot');

var _EmptyBlockDashboard = require('../../components/EmptyBlockDashboard');

var _EmptyBlockDashboard2 = _interopRequireDefault(_EmptyBlockDashboard);

require('./index.scss');

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

var AppState = _boot.stores.AppState;

var Announcement = function (_Component) {
  (0, _inherits3['default'])(Announcement, _Component);

  function Announcement(props) {
    (0, _classCallCheck3['default'])(this, Announcement);

    var _this = (0, _possibleConstructorReturn3['default'])(this, (Announcement.__proto__ || Object.getPrototypeOf(Announcement)).call(this, props));

    _this.state = {
      components: [],
      loading: false
    };
    return _this;
  }

  (0, _createClass3['default'])(Announcement, [{
    key: 'componentDidMount',
    value: function componentDidMount() {
      this.loadData();
    }
  }, {
    key: 'loadData',
    value: function loadData() {
      var _this2 = this;

      this.setState({ loading: true });
      _boot.axios.get('/wiki/v1/organizations/' + AppState.currentMenuType.organizationId + '/space/under').then(function (res) {
        _this2.setState({
          components: res.slice(0, 6),
          loading: false
        });
      });
    }
  }, {
    key: 'renderSpaces',
    value: function renderSpaces(space) {
      return _react2['default'].createElement(
        'div',
        { className: 'list', key: space.id },
        _react2['default'].createElement(
          'div',
          { className: 'wiki-dashboard-space-icon' },
          _react2['default'].createElement(_icon2['default'], { type: space.icon })
        ),
        _react2['default'].createElement(
          'div',
          { className: 'spaceSummary-wrap' },
          _react2['default'].createElement(
            _tooltip2['default'],
            { placement: 'topLeft', mouseEnterDelay: 0.5, title: space.name },
            _react2['default'].createElement(
              'a',
              { href: space.path, target: '_blank', className: 'spaceSummary text-overflow-hidden' },
              space.name
            )
          )
        )
      );
    }
  }, {
    key: 'renderContent',
    value: function renderContent() {
      var _this3 = this;

      var _state = this.state,
          loading = _state.loading,
          components = _state.components;
      var history = this.props.history;

      var urlParams = AppState.currentMenuType;
      if (loading) {
        return _react2['default'].createElement(
          'div',
          { className: 'loading-wrap' },
          _react2['default'].createElement(_spin2['default'], null)
        );
      }
      if (components && !components.length) {
        return _react2['default'].createElement(
          'div',
          { className: 'loading-wrap' },
          _react2['default'].createElement(
            _reactRouterDom.Link,
            {
              role: 'none',
              to: '/wiki/organization/space?type=' + urlParams.type + '&id=' + urlParams.id + '&name=' + encodeURIComponent(urlParams.name) + '&organizationId=' + urlParams.organizationId + '&createSpace=true'
            },
            '快速创建wiki空间'
          )
        );
      }
      return _react2['default'].createElement(
        'div',
        { className: 'lists' },
        components.map(function (space) {
          return _this3.renderSpaces(space);
        })
      );
    }
  }, {
    key: 'render',
    value: function render() {
      var components = this.state.components;
      var history = this.props.history;

      var urlParams = AppState.currentMenuType;
      return _react2['default'].createElement(
        'div',
        { className: 'c7n-wiki-dashboard-under-organization-space' },
        this.renderContent(),
        _react2['default'].createElement(
          _boot.DashBoardNavBar,
          null,
          _react2['default'].createElement(
            _reactRouterDom.Link,
            {
              role: 'none',
              to: '/wiki/organization/space?type=' + urlParams.type + '&id=' + urlParams.id + '&name=' + encodeURIComponent(urlParams.name) + '&organizationId=' + urlParams.organizationId
            },
            '转至Wiki管理'
          )
        )
      );
    }
  }]);
  return Announcement;
}(_react.Component);

exports['default'] = Announcement;