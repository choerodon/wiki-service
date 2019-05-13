'use strict';

var _regenerator = require('babel-runtime/regenerator');

var _regenerator2 = _interopRequireDefault(_regenerator);

var _asyncToGenerator2 = require('babel-runtime/helpers/asyncToGenerator');

var _asyncToGenerator3 = _interopRequireDefault(_asyncToGenerator2);

// 定义文件夹节点类型，包含本身路径名和子节点
// var folderNode = function(name, children) {
//   this.name = name;
//   this.children = children;
// };
// 遍历函数，传入一个节点，之后按照节点的name进行遍历，找出所有子节点
// function traversefolder(node) {
//   node = node instanceof folderNode ? node : new folderNode(node, null);

//   if (fs.statSync(node.name).isDirectory()) {
//     let arr = fs.readdirSync(node.name);
//     arr = arr.map(one => {
//       let ar = new folderNode(path.join(node.name, one), null);
//       return traversefolder(ar);
//     });
//     node.children = arr;
//   }
//   return node;
// }
// var traversefolder = function(dir) {
//   var results = [];
//   var list = fs.readdirSync(dir);
//   list.forEach(function(file) {
//     file = dir + '/' + file;
//     var stat = fs.statSync(file);
//     if (stat && stat.isDirectory())
//       results = results.concat(traversefolder(file));
//     else results.push(file);
//   });
//   return results;
// };

var traversefolder = function () {
  var _ref = (0, _asyncToGenerator3['default'])( /*#__PURE__*/_regenerator2['default'].mark(function _callee2(dir) {
    var _this = this;

    var subdirs, files;
    return _regenerator2['default'].wrap(function _callee2$(_context2) {
      while (1) {
        switch (_context2.prev = _context2.next) {
          case 0:
            _context2.next = 2;
            return readdir(dir);

          case 2:
            subdirs = _context2.sent;
            _context2.next = 5;
            return Promise.all(subdirs.map(function () {
              var _ref2 = (0, _asyncToGenerator3['default'])( /*#__PURE__*/_regenerator2['default'].mark(function _callee(subdir) {
                var res;
                return _regenerator2['default'].wrap(function _callee$(_context) {
                  while (1) {
                    switch (_context.prev = _context.next) {
                      case 0:
                        if (!(subdir !== 'System Volume Information')) {
                          _context.next = 10;
                          break;
                        }

                        res = resolve(dir, subdir);
                        _context.next = 4;
                        return stat(res);

                      case 4:
                        if (!_context.sent.isDirectory()) {
                          _context.next = 8;
                          break;
                        }

                        _context.t0 = traversefolder(res);
                        _context.next = 9;
                        break;

                      case 8:
                        _context.t0 = res;

                      case 9:
                        return _context.abrupt('return', _context.t0);

                      case 10:
                      case 'end':
                        return _context.stop();
                    }
                  }
                }, _callee, _this);
              }));

              return function (_x2) {
                return _ref2.apply(this, arguments);
              };
            }()));

          case 5:
            files = _context2.sent;
            return _context2.abrupt('return', files.reduce(function (a, f) {
              return a.concat(f);
            }, []));

          case 7:
          case 'end':
            return _context2.stop();
        }
      }
    }, _callee2, this);
  }));

  return function traversefolder(_x) {
    return _ref.apply(this, arguments);
  };
}();

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

var fs = require('fs');
var path = require('path');

var _require = require('util'),
    promisify = _require.promisify;

var _require2 = require('path'),
    resolve = _require2.resolve;

var readdir = promisify(fs.readdir);
var rename = promisify(fs.rename);
var stat = promisify(fs.stat);
module.exports = traversefolder;