'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _input = require('choerodon-ui/lib/input');

var _input2 = _interopRequireDefault(_input);

var _iconSelect = require('choerodon-ui/lib/icon-select');

var _iconSelect2 = _interopRequireDefault(_iconSelect);

var _classCallCheck2 = require('babel-runtime/helpers/classCallCheck');

var _classCallCheck3 = _interopRequireDefault(_classCallCheck2);

var _createClass2 = require('babel-runtime/helpers/createClass');

var _createClass3 = _interopRequireDefault(_createClass2);

var _possibleConstructorReturn2 = require('babel-runtime/helpers/possibleConstructorReturn');

var _possibleConstructorReturn3 = _interopRequireDefault(_possibleConstructorReturn2);

var _inherits2 = require('babel-runtime/helpers/inherits');

var _inherits3 = _interopRequireDefault(_inherits2);

var _form = require('choerodon-ui/lib/form');

var _form2 = _interopRequireDefault(_form);

var _modal = require('choerodon-ui/lib/modal');

var _modal2 = _interopRequireDefault(_modal);

require('choerodon-ui/lib/input/style');

require('choerodon-ui/lib/icon-select/style');

require('choerodon-ui/lib/form/style');

require('choerodon-ui/lib/modal/style');

var _react = require('react');

var _react2 = _interopRequireDefault(_react);

var _mobxReact = require('mobx-react');

var _reactRouterDom = require('react-router-dom');

var _reactIntl = require('react-intl');

var _boot = require('@choerodon/boot');

require('./OperateSpace.scss');

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

var Sidebar = _modal2['default'].Sidebar;
var AppState = _boot.stores.AppState;

var FormItem = _form2['default'].Item;
var formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 100 }
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 10 }
  }
};
var inputWidth = 512;

var AddSpace = function (_Component) {
  (0, _inherits3['default'])(AddSpace, _Component);

  function AddSpace(props) {
    (0, _classCallCheck3['default'])(this, AddSpace);

    var _this = (0, _possibleConstructorReturn3['default'])(this, (AddSpace.__proto__ || Object.getPrototypeOf(AddSpace)).call(this, props));

    _this.checkName = function (rule, value, callback) {
      var intl = _this.props.intl;

      _boot.axios.get('/wiki/v1/organizations/' + AppState.currentMenuType.organizationId + '/space/check?name=' + value).then(function (res) {
        if (res.failed) {
          callback(intl.formatMessage({ id: 'wiki.name.check.exist' }));
        } else {
          callback();
        }
      });
    };

    _this.handleProptError = function (error) {
      if (error && error.failed) {
        Choerodon.prompt(error.message);
        return false;
      } else {
        return true;
      }
    };

    _this.state = {
      originUsers: [],
      selectLoading: false,
      createLoading: false
    };
    return _this;
  }

  (0, _createClass3['default'])(AddSpace, [{
    key: 'handleOk',
    value: function handleOk(e) {
      var _this2 = this;

      e.preventDefault();
      this.props.form.validateFields(function (err, values) {
        if (!err) {
          var intl = _this2.props.intl;
          var icon = values.icon,
              name = values.name,
              description = values.description;

          var component = {
            icon: icon,
            name: name,
            description: description
          };
          _this2.setState({ createLoading: true });
          _boot.axios.post('/wiki/v1/organizations/' + AppState.currentMenuType.organizationId + '/space', component).then(function (datas) {
            var res = _this2.handleProptError(datas);
            if (res) {
              _this2.setState({
                createLoading: false
              });
              _this2.props.onOk();
            } else {
              _this2.setState({
                createLoading: false
              });
              _this2.props.onOk();
            }
          })['catch'](function (error) {
            _this2.setState({
              createLoading: false
            });
            Choerodon.prompt(Choerodon.getMessage('创建空间失败!', 'Failed to create space!'));
          });
        }
      });
    }
  }, {
    key: 'render',
    value: function render() {
      var getFieldDecorator = this.props.form.getFieldDecorator;
      var intl = this.props.intl;

      return _react2['default'].createElement(
        Sidebar,
        {
          className: 'c7n-component-component',
          title: _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'wiki.create.space' }),
          okText: _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'create' }),
          cancelText: _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'cancel' }),
          visible: this.props.visible || false,
          confirmLoading: this.state.createLoading,
          onOk: this.handleOk.bind(this),
          onCancel: this.props.onCancel.bind(this)
        },
        _react2['default'].createElement(
          _boot.Content,
          {
            style: {
              padding: 0
            },
            title: '\u5728\u7EC4\u7EC7"' + AppState.currentMenuType.name + '"\u4E2D\u521B\u5EFA\u7A7A\u95F4',
            description: _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'wiki.create.org.description' })
          },
          _react2['default'].createElement(
            _form2['default'],
            { style: { width: 512 } },
            _react2['default'].createElement(
              FormItem,
              formItemLayout,
              getFieldDecorator('icon', {
                rules: [{
                  required: true,
                  message: intl.formatMessage({ id: 'required' })
                }],
                validateTrigger: 'onChange'
              })(_react2['default'].createElement(_iconSelect2['default'], {
                label: _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'wiki.space.icon' }),
                style: { width: inputWidth }
              }))
            ),
            _react2['default'].createElement(
              FormItem,
              null,
              getFieldDecorator('name', {
                rules: [{
                  required: true,
                  message: intl.formatMessage({ id: 'required' })
                }, {
                  validator: this.checkName
                }]
              })(_react2['default'].createElement(_input2['default'], { label: _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'wiki.space.name' }), maxLength: 30 }))
            )
          )
        )
      );
    }
  }]);
  return AddSpace;
}(_react.Component);

exports['default'] = _form2['default'].create({})((0, _reactRouterDom.withRouter)((0, _reactIntl.injectIntl)(AddSpace)));