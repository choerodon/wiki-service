'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _form = require('choerodon-ui/lib/form');

var _form2 = _interopRequireDefault(_form);

var _modal = require('choerodon-ui/lib/modal');

var _modal2 = _interopRequireDefault(_modal);

var _spin = require('choerodon-ui/lib/spin');

var _spin2 = _interopRequireDefault(_spin);

var _table = require('choerodon-ui/lib/table');

var _table2 = _interopRequireDefault(_table);

var _button = require('choerodon-ui/lib/button');

var _button2 = _interopRequireDefault(_button);

var _tooltip = require('choerodon-ui/lib/tooltip');

var _tooltip2 = _interopRequireDefault(_tooltip);

var _extends2 = require('babel-runtime/helpers/extends');

var _extends3 = _interopRequireDefault(_extends2);

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

var _class;

require('choerodon-ui/lib/form/style');

require('choerodon-ui/lib/modal/style');

require('choerodon-ui/lib/spin/style');

require('choerodon-ui/lib/table/style');

require('choerodon-ui/lib/button/style');

require('choerodon-ui/lib/tooltip/style');

require('choerodon-ui/lib/icon/style');

var _react = require('react');

var _react2 = _interopRequireDefault(_react);

var _mobxReact = require('mobx-react');

var _reactRouterDom = require('react-router-dom');

var _reactIntl = require('react-intl');

var _boot = require('@choerodon/boot');

require('./WikiSettingHome.scss');

var _AddSpace = require('../OperateWikiSetting/AddSpace');

var _AddSpace2 = _interopRequireDefault(_AddSpace);

var _EditSpace = require('../OperateWikiSetting/EditSpace');

var _EditSpace2 = _interopRequireDefault(_EditSpace);

var _SmartTooltip = require('../../../../components/SmartTooltip');

var _SmartTooltip2 = _interopRequireDefault(_SmartTooltip);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

var AppState = _boot.stores.AppState;

var TextOverFlowStyle = {
  whiteSpace: 'nowrap',
  overflow: 'hidden',
  textOverflow: 'ellipsis'
};

var WikiSettingHome = (0, _mobxReact.observer)(_class = function (_Component) {
  (0, _inherits3['default'])(WikiSettingHome, _Component);

  function WikiSettingHome(props) {
    (0, _classCallCheck3['default'])(this, WikiSettingHome);

    var _this = (0, _possibleConstructorReturn3['default'])(this, (WikiSettingHome.__proto__ || Object.getPrototypeOf(WikiSettingHome)).call(this, props));

    _this.openRemove = function (record) {
      _this.setState({
        openRemove: true,
        currentComponentId: record.id
      });
    };

    _this.closeRemove = function () {
      _this.setState({ openRemove: false });
    };

    _this.handleDelete = function () {
      _this.setState({
        confirmShow: true
      });
      _boot.axios['delete']('/wiki/v1/organizations/' + AppState.currentMenuType.organizationId + '/space/' + _this.state.currentComponentId).then(function (datas) {
        var res = _this.handleProptError(datas);
        if (res) {
          _this.setState({
            openRemove: false,
            confirmShow: false
          });
          _this.loadComponents();
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

    _this.syncShowModal = function () {
      _this.setState({
        syncVisible: true
      });
    };

    _this.handleOk = function () {
      _this.setState({
        syncLoading: true
      });
      _boot.axios.get('/wiki/v1/organizations/' + AppState.currentMenuType.organizationId + '/space/sync').then(function (datas) {
        var res = _this.handleProptError(datas);
        if (res) {
          _this.setState({
            syncVisible: false,
            syncLoading: false
          });
          _this.loadComponents();
        } else {
          _this.setState({
            syncVisible: false,
            syncLoading: false
          });
        }
      });
    };

    _this.handleCancel = function () {
      _this.setState({
        syncVisible: false
      });
    };

    _this.syncOrgShowModal = function () {
      _this.setState({
        syncOrgVisible: true
      });
    };

    _this.orgHandleOk = function () {
      _this.setState({
        syncOrgLoading: true
      });
      _boot.axios.get('/wiki/v1/organizations/' + AppState.currentMenuType.organizationId + '/space/sync_org').then(function (datas) {
        var res = _this.handleProptError(datas);
        if (res) {
          _this.setState({
            syncOrgVisible: false,
            syncOrgLoading: false
          });
          _this.loadComponents();
        } else {
          _this.setState({
            syncOrgVisible: false,
            syncOrgLoading: false
          });
        }
      });
    };

    _this.orgHandleCancel = function () {
      _this.setState({
        syncOrgVisible: false
      });
    };

    _this.syncUnderOrgShowModal = function (record) {
      _this.setState({
        syncUnderOrgVisible: true,
        currentComponentId: record.id
      });
    };

    _this.underOrgHandleOk = function () {
      _this.setState({
        syncUnderOrgLoading: true
      });
      _boot.axios.put('/wiki/v1/organizations/' + AppState.currentMenuType.organizationId + '/space/sync/' + _this.state.currentComponentId).then(function (datas) {
        var res = _this.handleProptError(datas);
        if (res) {
          _this.setState({
            syncUnderOrgVisible: false,
            syncUnderOrgLoading: false
          });
        } else {
          _this.setState({
            syncUnderOrgVisible: false,
            syncUnderOrgLoading: false
          });
        }
        _this.loadComponents();
      })['catch'](function (error) {
        _this.setState({
          syncUnderOrgVisible: false,
          syncUnderOrgLoading: false
        });
        Choerodon.prompt(Choerodon.getMessage('同步空间失败', 'Synchronization space failed'));
      });
    };

    _this.underOrgHandleCancel = function () {
      _this.setState({
        syncUnderOrgVisible: false
      });
    };

    var Request = _this.GetRequest(_this.props.location.search);
    var createSpace = Request.createSpace;

    _this.state = {
      components: [],
      component: {},
      currentComponentId: undefined,
      loading: false,
      confirmShow: false,
      editComponentShow: false,
      createComponentShow: createSpace ? true : false,
      openRemove: false,
      syncVisible: false,
      syncLoading: false,
      syncOrgVisible: false,
      syncOrgLoading: false,
      syncUnderOrgVisible: false,
      syncUnderOrgLoading: false
    };
    return _this;
  }

  (0, _createClass3['default'])(WikiSettingHome, [{
    key: 'GetRequest',
    value: function GetRequest(url) {
      var theRequest = {};
      if (url.indexOf('?') !== -1) {
        var str = url.split('?')[1];
        var strs = str.split('&');
        for (var i = 0; i < strs.length; i += 1) {
          theRequest[strs[i].split('=')[0]] = decodeURI(strs[i].split('=')[1]);
        }
      }
      return theRequest;
    }
  }, {
    key: 'componentDidMount',
    value: function componentDidMount() {
      this.loadComponents();
      window.console.log(AppState.currentMenuType);
    }
  }, {
    key: 'showComponent',
    value: function showComponent(record) {
      this.setState({
        editComponentShow: true,
        currentComponentId: record.id
      });
    }
  }, {
    key: 'getLastName',
    value: function getLastName(path) {
      var arrlen = path.split('/');
      var backstr = "";
      for (var a = 5; a < arrlen.length; a++) {
        backstr = backstr + arrlen[a];
        if (a != arrlen.length - 1) {
          backstr = backstr + "/";
        }
      }
      return backstr;
    }
  }, {
    key: 'loadComponents',
    value: function loadComponents() {
      var _this2 = this;

      this.setState({
        loading: true //需要加载数据时设true
      });
      _boot.axios.post('/wiki/v1/organizations/' + AppState.currentMenuType.organizationId + '/space/list_by_options?sort=id%2Cdesc').then(function (res) {
        _this2.setState({
          components: res.content,
          loading: false
        });
      })['catch'](function (error) {
        window.console.warn('load spaces failed, check your organization and project are correct, or please try again later');
      });
    }
  }, {
    key: 'render',
    value: function render() {
      var _this3 = this;

      var menu = AppState.currentMenuType;
      var projectName = menu.name;
      var type = menu.type,
          projectId = menu.id,
          orgId = menu.organizationId;

      var column = [{
        title: _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'wiki.column.name' }),
        dataIndex: 'name',
        key: 'name',
        className: 'name-column',
        render: function render(text, record) {
          return _react2['default'].createElement(
            'div',
            { style: { overflow: 'hidden', display: 'flex' } },
            _react2['default'].createElement(_icon2['default'], { type: record.icon, style: { marginRight: 5 } }),
            _react2['default'].createElement(
              _SmartTooltip2['default'],
              null,
              record.name
            )
          );
        }
      }, {
        title: _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'wiki.column.path' }),
        key: 'path',
        // width: '15%',
        render: function render(test, record) {
          return _react2['default'].createElement(
            'div',
            { style: (0, _extends3['default'])({ width: '100%' }, TextOverFlowStyle) },
            _react2['default'].createElement(
              _tooltip2['default'],
              { placement: 'topLeft', mouseEnterDelay: 0.5, title: record.path },
              record.status === 'success' ? _react2['default'].createElement(
                'a',
                { href: record.path, target: '_blank', style: { dispaly: 'block', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', marginBottom: 0 } },
                '../',
                _this3.getLastName(record.path)
              ) : _react2['default'].createElement(
                'span',
                { style: { dispaly: 'none', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', marginBottom: 0 } },
                '../',
                _this3.getLastName(record.path)
              )
            )
          );
        }
      }, {
        title: _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'wiki.column.status' }),
        key: 'status',
        // width: '15%', 
        render: function render(record) {
          var statusDom = null;
          switch (record.status) {
            case 'operating':
              statusDom = _react2['default'].createElement(
                'div',
                { className: 'c7n-wiki-status c7n-wiki-status-operating' },
                _react2['default'].createElement(
                  'div',
                  null,
                  _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'operating' })
                )
              );
              break;
            case 'success':
              statusDom = _react2['default'].createElement(
                'div',
                { className: 'c7n-wiki-status c7n-wiki-status-success' },
                _react2['default'].createElement(
                  'div',
                  null,
                  _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'success' })
                )
              );
              break;
            case 'deleted':
              statusDom = _react2['default'].createElement(
                'div',
                { className: 'c7n-wiki-status c7n-wiki-status-deleted' },
                _react2['default'].createElement(
                  'div',
                  null,
                  _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'deleted' })
                )
              );
              break;
            case 'failed':
              statusDom = _react2['default'].createElement(
                'div',
                { className: 'c7n-wiki-status c7n-wiki-status-failed' },
                _react2['default'].createElement(
                  'div',
                  null,
                  _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'failed' })
                )
              );
              break;
            default:
              statusDom = null;
          }
          return statusDom;
        }
      }, {
        key: 'action',
        //  width: '10%',
        render: function render(record) {
          var editDom = null;
          var deletDom = null;
          var syncDom = null;
          var orgSyncDom = null;
          if (record.resourceType === 'organization') {
            if (record.status === 'failed') {
              syncDom = _react2['default'].createElement(
                _react2['default'].Fragment,
                null,
                _react2['default'].createElement(
                  _tooltip2['default'],
                  { trigger: 'hover', placement: 'bottom', title: _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'retry' }) },
                  _react2['default'].createElement(
                    _button2['default'],
                    { shape: 'circle', size: 'small', funcType: 'flat', onClick: _this3.syncOrgShowModal },
                    _react2['default'].createElement('span', { className: 'icon icon-sync' })
                  )
                )
              );
            }
          } else {
            switch (record.status) {
              case 'operating':
                editDom = _react2['default'].createElement(
                  _tooltip2['default'],
                  { trigger: 'hover', placement: 'bottom', title: _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'wiki.operating' }) },
                  _react2['default'].createElement(
                    _button2['default'],
                    { shape: 'circle', size: 'small', funcType: 'flat' },
                    _react2['default'].createElement('span', { className: 'icon icon-mode_edit c7n-app-icon-disabled' })
                  )
                );
                deletDom = _react2['default'].createElement(
                  _tooltip2['default'],
                  { trigger: 'hover', placement: 'bottom', title: _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'wiki.operating' }) },
                  _react2['default'].createElement(
                    _button2['default'],
                    { shape: 'circle', size: 'small', funcType: 'flat' },
                    _react2['default'].createElement('span', { className: 'icon icon-delete_forever c7n-app-icon-disabled' })
                  )
                );
                break;
              case 'failed':
                orgSyncDom = _react2['default'].createElement(
                  _react2['default'].Fragment,
                  null,
                  _react2['default'].createElement(
                    _tooltip2['default'],
                    { trigger: 'hover', placement: 'bottom', title: _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'retry' }) },
                    _react2['default'].createElement(
                      _button2['default'],
                      { shape: 'circle', size: 'small', funcType: 'flat', onClick: _this3.syncUnderOrgShowModal.bind(_this3, record) },
                      _react2['default'].createElement('span', { className: 'icon icon-sync' })
                    )
                  )
                );
                editDom = _react2['default'].createElement(
                  _tooltip2['default'],
                  { trigger: 'hover', placement: 'bottom', title: _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'wiki.failed' }) },
                  _react2['default'].createElement(
                    _button2['default'],
                    { shape: 'circle', size: 'small', funcType: 'flat' },
                    _react2['default'].createElement('span', { className: 'icon icon-mode_edit c7n-app-icon-disabled' })
                  )
                );
                deletDom = _react2['default'].createElement(
                  _react2['default'].Fragment,
                  null,
                  _react2['default'].createElement(
                    _tooltip2['default'],
                    { trigger: 'hover', placement: 'bottom', title: _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'delete' }) },
                    _react2['default'].createElement(
                      _button2['default'],
                      { shape: 'circle', size: 'small', funcType: 'flat', onClick: _this3.openRemove.bind(_this3, record) },
                      _react2['default'].createElement('span', { className: 'icon icon-delete_forever' })
                    )
                  )
                );
                break;
              case 'success':
                editDom = _react2['default'].createElement(
                  _react2['default'].Fragment,
                  null,
                  _react2['default'].createElement(
                    _tooltip2['default'],
                    { trigger: 'hover', placement: 'bottom', title: _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'editor' }) },
                    _react2['default'].createElement(
                      _button2['default'],
                      { shape: 'circle', size: 'small', funcType: 'flat', onClick: _this3.showComponent.bind(_this3, record) },
                      _react2['default'].createElement('span', { className: 'icon icon-mode_edit' })
                    )
                  )
                );
                deletDom = _react2['default'].createElement(
                  _react2['default'].Fragment,
                  null,
                  _react2['default'].createElement(
                    _tooltip2['default'],
                    { trigger: 'hover', placement: 'bottom', title: _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'delete' }) },
                    _react2['default'].createElement(
                      _button2['default'],
                      { shape: 'circle', size: 'small', funcType: 'flat', onClick: _this3.openRemove.bind(_this3, record) },
                      _react2['default'].createElement('span', { className: 'icon icon-delete_forever' })
                    )
                  )
                );
                break;
            }
          }
          return _react2['default'].createElement(
            'div',
            null,
            _react2['default'].createElement(
              _boot.Permission,
              {
                service: ['wiki-service.wiki-organization-space.sync'],
                type: type,
                projectId: projectId,
                organizationId: orgId
              },
              orgSyncDom
            ),
            _react2['default'].createElement(
              _boot.Permission,
              {
                service: ['wiki-service.wiki-scanning.syncOrg'],
                type: type,
                projectId: projectId,
                organizationId: orgId
              },
              syncDom
            ),
            _react2['default'].createElement(
              _boot.Permission,
              {
                service: ['wiki-service.wiki-organization-space.update'],
                type: type,
                projectId: projectId,
                organizationId: orgId
              },
              editDom
            ),
            _react2['default'].createElement(
              _boot.Permission,
              {
                service: ['wiki-service.wiki-organization-space.delete'],
                type: type,
                projectId: projectId,
                organizationId: orgId
              },
              deletDom
            )
          );
        }
      }];
      return _react2['default'].createElement(
        _boot.Page,
        {
          service: ['wiki-service.wiki-organization-space.create', 'wiki-service.wiki-organization-space.pageByOptions', 'wiki-service.wiki-organization-space.query', 'wiki-service.wiki-organization-space.update', 'wiki-service.wiki-organization-space.checkName', 'wiki-service.wiki-organization-space.delete', 'wiki-service.wiki-organization-space.sync', 'wiki-service.wiki-scanning.syncOrg'],
          className: 'c7n-wiki'
        },
        _react2['default'].createElement(
          _boot.Header,
          { title: _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'wiki.header.title' }) },
          _react2['default'].createElement(
            _boot.Permission,
            {
              service: ['wiki-service.wiki-organization-space.create'],
              type: type,
              projectId: projectId,
              organizationId: orgId
            },
            _react2['default'].createElement(
              _button2['default'],
              { funcType: 'flat', onClick: function onClick() {
                  return _this3.setState({ createComponentShow: true });
                } },
              _react2['default'].createElement(_icon2['default'], { type: 'playlist_add icon' }),
              _react2['default'].createElement(
                'span',
                null,
                _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'wiki.create.space' })
              )
            )
          ),
          this.state.components.length === 0 ? _react2['default'].createElement(
            _boot.Permission,
            {
              service: ['wiki-service.wiki-organization-space.sync'],
              type: type,
              projectId: projectId,
              organizationId: orgId
            },
            _react2['default'].createElement(
              _button2['default'],
              { funcType: 'flat', onClick: this.syncShowModal },
              _react2['default'].createElement(_icon2['default'], { type: 'sync icon' }),
              _react2['default'].createElement(
                'span',
                null,
                _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'sync' })
              )
            )
          ) : null,
          _react2['default'].createElement(
            _boot.Permission,
            {
              service: ['wiki-service.wiki-organization-space.pageByOptions'],
              type: type,
              projectId: projectId,
              organizationId: orgId
            },
            _react2['default'].createElement(
              _button2['default'],
              { funcType: 'flat', onClick: function onClick() {
                  return _this3.loadComponents();
                } },
              _react2['default'].createElement(_icon2['default'], { type: 'autorenew icon' }),
              _react2['default'].createElement(
                'span',
                null,
                _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'refresh' })
              )
            )
          )
        ),
        _react2['default'].createElement(
          _boot.Content,
          { code: 'wiki', value: _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'wiki.link' }),
            title: _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'wiki.title' }),
            description: _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'wiki.description' })
          },
          _react2['default'].createElement(
            _spin2['default'],
            { spinning: this.state.loading },
            _react2['default'].createElement(_table2['default'], {
              columns: column,
              dataSource: this.state.components,
              scroll: { x: true },
              rowKey: function rowKey(record) {
                return record.id;
              },
              defaultExpandAllRows: true
            })
          ),
          this.state.createComponentShow ? _react2['default'].createElement(_AddSpace2['default'], {
            visible: this.state.createComponentShow,
            onCancel: function onCancel() {
              return _this3.setState({ createComponentShow: false });
            },
            onOk: function onOk() {
              _this3.loadComponents();
              _this3.setState({
                createComponentShow: false
              });
            }
          }) : null,
          this.state.editComponentShow ? _react2['default'].createElement(_EditSpace2['default'], {
            id: this.state.currentComponentId,
            visible: this.state.editComponentShow,
            onCancel: function onCancel() {
              return _this3.setState({ editComponentShow: false });
            },
            onOk: function onOk() {
              _this3.loadComponents();
              _this3.setState({
                editComponentShow: false
              });
            }
          }) : null,
          _react2['default'].createElement(
            _modal2['default'],
            {
              closable: false,
              visible: this.state.openRemove,
              title: _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'wiki.delete.space' }),
              footer: [_react2['default'].createElement(
                _button2['default'],
                { key: 'back', onClick: this.closeRemove },
                _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'cancel' })
              ), _react2['default'].createElement(
                _button2['default'],
                { key: 'submit', type: 'danger', loading: this.state.confirmShow, onClick: this.handleDelete },
                _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'delete' })
              )]
            },
            _react2['default'].createElement(
              'p',
              null,
              _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'wiki.delete.tooltip' }),
              '\uFF1F'
            )
          ),
          _react2['default'].createElement(
            _modal2['default'],
            {
              closable: false,
              title: _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'wiki.sync.space' }),
              visible: this.state.syncVisible,
              onOk: this.handleOk,
              confirmLoading: this.state.syncLoading,
              onCancel: this.handleCancel
            },
            _react2['default'].createElement(
              'p',
              null,
              _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'wiki.sync.tooltip' }),
              '\uFF1F'
            )
          ),
          _react2['default'].createElement(
            _modal2['default'],
            {
              closable: false,
              title: _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'wiki.sync.space' }),
              visible: this.state.syncOrgVisible,
              onOk: this.orgHandleOk,
              confirmLoading: this.state.syncOrgLoading,
              onCancel: this.orgHandleCancel
            },
            _react2['default'].createElement(
              'p',
              null,
              _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'wiki.sync.org.tooltip' }),
              '\uFF1F'
            )
          ),
          _react2['default'].createElement(
            _modal2['default'],
            {
              closable: false,
              title: _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'wiki.sync.space' }),
              visible: this.state.syncUnderOrgVisible,
              onOk: this.underOrgHandleOk,
              confirmLoading: this.state.syncUnderOrgLoading,
              onCancel: this.underOrgHandleCancel
            },
            _react2['default'].createElement(
              'p',
              null,
              _react2['default'].createElement(_reactIntl.FormattedMessage, { id: 'wiki.sync.under.org.tooltip' }),
              '\uFF1F'
            )
          )
        )
      );
    }
  }]);
  return WikiSettingHome;
}(_react.Component)) || _class;

exports['default'] = _form2['default'].create({})((0, _reactRouterDom.withRouter)((0, _reactIntl.injectIntl)(WikiSettingHome)));