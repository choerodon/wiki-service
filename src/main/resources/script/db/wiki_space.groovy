package script.db

databaseChangeLog(logicalFilePath: 'wiki_space.groovy') {
    changeSet(author: 'Zenger', id: '2018-07-02-create-table') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'WIKI_SPACE_S', startValue:"1")
        }
        createTable(tableName: "WIKI_SPACE", remarks: '空间') {
            column(name: 'ID', type: 'BIGINT UNSIGNED', remarks: '主键，ID', autoIncrement: true) {
                constraints(primaryKey: true)
            }
            column(name: 'RESOURCE_ID', type: 'BIGINT UNSIGNED', remarks: '资源 ID')
            column(name: 'RESOURCE_TYPE', type: 'VARCHAR(32)', remarks: '资源类型')
            column(name: 'NAME', type: 'VARCHAR(64)', remarks: '空间名称')
            column(name: 'ICON', type: 'VARCHAR(64)', remarks: '空间图标')
            column(name: 'PATH', type: 'VARCHAR(256)', remarks: '空间地址')
            column(name: 'IS_SYNCHRO', type: 'TINYINT UNSIGNED', remarks: '状态')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(author: 'Zenger', id: '2018-07-27-add-column') {
        dropColumn(columnName: "IS_SYNCHRO", tableName: "WIKI_SPACE")

        addColumn(tableName: 'WIKI_SPACE') {
            column(name: 'STATUS', type: 'VARCHAR(64)', remarks: '空间创建状态',afterColumn: 'PATH')
        }

        addUniqueConstraint(tableName: 'WIKI_SPACE', constraintName: 'U_NAME', columnNames: 'RESOURCE_ID,RESOURCE_TYPE,NAME')
    }

    changeSet(author: 'Zenger',id: '2018-08-16-sql') {
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "UPDATE WIKI_SPACE ws SET ws.STATUS = 'success';"
        }
    }

    changeSet(author: 'Zenger',id: '2018-09-18-addPrimaryKey') {
        dropUniqueConstraint(tableName: 'WIKI_SPACE', constraintName: 'U_NAME', uniqueColumns: 'RESOURCE_ID,RESOURCE_TYPE,NAME')
        addUniqueConstraint(tableName: 'WIKI_SPACE', constraintName: 'UK_WIKI_SPACE_U1', columnNames: 'RESOURCE_ID,RESOURCE_TYPE,NAME')
    }
}