'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _tooltip = require('choerodon-ui/lib/tooltip');

var _tooltip2 = _interopRequireDefault(_tooltip);

var _extends2 = require('babel-runtime/helpers/extends');

var _extends3 = _interopRequireDefault(_extends2);

var _classCallCheck2 = require('babel-runtime/helpers/classCallCheck');

var _classCallCheck3 = _interopRequireDefault(_classCallCheck2);

var _createClass2 = require('babel-runtime/helpers/createClass');

var _createClass3 = _interopRequireDefault(_createClass2);

var _possibleConstructorReturn2 = require('babel-runtime/helpers/possibleConstructorReturn');

var _possibleConstructorReturn3 = _interopRequireDefault(_possibleConstructorReturn2);

var _inherits2 = require('babel-runtime/helpers/inherits');

var _inherits3 = _interopRequireDefault(_inherits2);

var _class, _temp2;

require('choerodon-ui/lib/tooltip/style');

var _react = require('react');

var _react2 = _interopRequireDefault(_react);

var _propTypes = require('prop-types');

var _propTypes2 = _interopRequireDefault(_propTypes);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

var defaultStyle = {
  overflow: 'hidden',
  textOverflow: 'ellipsis',
  whiteSpace: 'nowrap'
};
var SmartTooltip = (_temp2 = _class = function (_Component) {
  (0, _inherits3['default'])(SmartTooltip, _Component);

  function SmartTooltip() {
    var _ref;

    var _temp, _this, _ret;

    (0, _classCallCheck3['default'])(this, SmartTooltip);

    for (var _len = arguments.length, args = Array(_len), _key = 0; _key < _len; _key++) {
      args[_key] = arguments[_key];
    }

    return _ret = (_temp = (_this = (0, _possibleConstructorReturn3['default'])(this, (_ref = SmartTooltip.__proto__ || Object.getPrototypeOf(SmartTooltip)).call.apply(_ref, [this].concat(args))), _this), _this.state = {
      overflow: false
    }, _this.checkOverflow = function () {
      if (_this.container) {
        var _this$container = _this.container,
            scrollWidth = _this$container.scrollWidth,
            clientWidth = _this$container.clientWidth;

        var isOverflow = scrollWidth > clientWidth;
        // console.log(scrollWidth, clientWidth);     
        if (_this.state.overflow !== isOverflow) {
          _this.setState({
            overflow: isOverflow
          });
        }
      }
    }, _this.saveRef = function (name) {
      return function (ref) {
        _this[name] = ref;
      };
    }, _this.renderContent = function () {
      var _this$props = _this.props,
          title = _this$props.title,
          children = _this$props.children,
          style = _this$props.style,
          width = _this$props.width,
          placement = _this$props.placement;
      // console.log(this.props);

      var overflow = _this.state.overflow;

      var dom = _react2['default'].createElement('div', (0, _extends3['default'])({}, _this.props, { style: (0, _extends3['default'])({}, defaultStyle, style, { width: width }), title: null, ref: _this.saveRef('container') }));
      return overflow ? _react2['default'].createElement(
        _tooltip2['default'],
        { placement: placement, title: title || children },
        dom
      ) : dom;
    }, _temp), (0, _possibleConstructorReturn3['default'])(_this, _ret);
  }

  (0, _createClass3['default'])(SmartTooltip, [{
    key: 'componentDidMount',
    value: function componentDidMount() {
      this.checkOverflow();
    }
  }, {
    key: 'componentDidUpdate',
    value: function componentDidUpdate(prevProps, prevState) {
      this.checkOverflow();
    }
  }, {
    key: 'render',
    value: function render() {
      return this.renderContent();
    }
  }]);
  return SmartTooltip;
}(_react.Component), _class.defaultProps = {
  style: {},
  placement: 'topLeft'
}, _temp2);


SmartTooltip.propTypes = {
  placement: _propTypes2['default'].string,
  style: _propTypes2['default'].object
};

exports['default'] = SmartTooltip;