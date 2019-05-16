// zh_CN.js
// 文档地址前缀
const docServer = 'http://choerodon.io';
// 界面标题描述统一管理
const pageDetail = {
    'wiki.title':'Wiki简介',
    'wiki.description':'Wiki是为项目和组织提供知识管理和共享的平台。',
    'wiki.link':`${docServer}/zh/docs/user-guide/wiki/space/`,
};

const zh_CN = {
    //public
    refresh: '刷新',
    operating: '处理中',
    success:'处理成功',
    deleted: '已删除',
    failed:'处理失败', 
    create: '创建',
    edit:'修改',
    editor:'编辑',
    delete:'删除',
    cancel: '取消',
    required: '该字段是必输的',
    sync: '同步',
    retry: '重试',

    "learnmore": "了解更多", // 必须有这个字段
    'global.menusetting.icon': '空间图标',

    //space
    'wiki.column.icon':'空间图标',
    'wiki.column.name':'空间名称',
    'wiki.column.path':'空间地址',
    'wiki.column.status':'状态',
    'wiki.header.title':'Wiki管理',
    'wiki.create.space':'创建空间',
    'wiki.create.org.description':'为你的组织创建一个空间。',
    'wiki.create.project.description':'为你的项目创建一个空间。',
    'wiki.space.icon':'空间图标',
    'wiki.space.name':'空间名称',
    'wiki.name.check.exist':'空间名称已被使用!',
    'wiki.create.space.error':'创建空间失败!',
    'wiki.edit.space.error':'修改空间失败!',
    'wiki.see.space':'查看空间',
    'wiki.eidt.description':'你可以修改空间图标。',
    'wiki.operating': '处理中，请耐心等待',
    'wiki.failed': '不可编辑',
    'wiki.delete.space': '删除空间',
    'wiki.delete.tooltip': '您确定要将此空间删除吗',
    'wiki.sync.space' : '同步空间',
    'wiki.sync.tooltip' : '同步该组织和组织下项目对应的空间',
    'wiki.sync.org.tooltip' : '同步组织对应的空间',
    'wiki.sync.project.tooltip' : '同步项目对应的空间',
    'wiki.sync.under.org.tooltip' : '同步组织下的空间',
    'wiki.sync.under.project.tooltip' : '同步项目下的空间',
    'wiki.sync.space.error' : '同步空间失败',
  
   ...pageDetail,
};
export default zh_CN;

