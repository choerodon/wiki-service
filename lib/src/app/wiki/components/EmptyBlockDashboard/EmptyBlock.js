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

var _react = require('react');

var _react2 = _interopRequireDefault(_react);

require('./EmptyBlock.scss');

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

var EmptyBlock = function (_Component) {
  (0, _inherits3['default'])(EmptyBlock, _Component);

  function EmptyBlock() {
    (0, _classCallCheck3['default'])(this, EmptyBlock);
    return (0, _possibleConstructorReturn3['default'])(this, (EmptyBlock.__proto__ || Object.getPrototypeOf(EmptyBlock)).apply(this, arguments));
  }

  (0, _createClass3['default'])(EmptyBlock, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          pic = _props.pic,
          des = _props.des;

      return _react2['default'].createElement(
        'div',
        {
          className: 'c7n-emptyBlock'
        },
        _react2['default'].createElement(
          'div',
          { className: 'c7n-imgWrap' },
          _react2['default'].createElement('img', { src: pic, alt: '', className: 'c7n-img' })
        ),
        _react2['default'].createElement(
          'div',
          { className: 'c7n-des' },
          des || ''
        )
      );
    }
  }]);
  return EmptyBlock;
}(_react.Component);

exports['default'] = EmptyBlock;