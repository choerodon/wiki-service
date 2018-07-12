package script.db

databaseChangeLog(logicalFilePath: 'dba/wiki_space.groovy') {
    changeSet(author: 'Zenger', id: '2018-07-02-create-table') {
        createTable(tableName: "wiki_space", remarks: '空间') {
            column(name: 'id', type: 'BIGINT UNSIGNED', remarks: '主键，ID', autoIncrement: true) {
                constraints(primaryKey: true)
            }
            column(name: 'resource_id', type: 'BIGINT UNSIGNED', remarks: '资源 ID')
            column(name: 'resource_type', type: 'VARCHAR(32)', remarks: '资源类型')
            column(name: 'name', type: 'VARCHAR(64)', remarks: '空间名称')
            column(name: 'icon', type: 'VARCHAR(64)', remarks: '空间图标')
            column(name: 'path', type: 'VARCHAR(256)', remarks: '空间地址')
            column(name: 'is_synchro', type: 'TINYINT UNSIGNED', remarks: '状态')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}