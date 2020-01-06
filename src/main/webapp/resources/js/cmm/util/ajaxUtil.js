import xss from 'xss';

import { showLoading, hideLoading } from './ec-common';
import { openDialog } from './ec-popup';

function call(p) {
    let parameters;
    let processData = true;
    let async = false;

    if (_.isEmpty(p.contentType) && !_.isBoolean(p.contentType)) {
        parameters = { _dataset_: JSON.stringify(p.data) };
    } else if (_.isEqual(p.contentType, 'multipart/form-data') || _.isEqual(p.contentType, false)) {
        parameters = p.data;
        processData = false;
    }

    if (_.isBoolean(p.async)) {
        async = p.async;
    }

    if (async) {
        if (p.showLoading !== false) {
            showLoading();
        }
    }

    let pgmId;
    let menuId;
    try {
        pgmId = window[top.tabContainer.getActiveTabIndex() + 1].GFN_CONTEXT_INFO.pgmId;
        menuId = window[top.tabContainer.getActiveTabIndex() + 1].GFN_CONTEXT_INFO.menuId;
    } catch (error) {
        pgmId = '';
        menuId = '';
    }

    $.ajax({
        url: xss(p.url),
        data: parameters,
        method: p.method || 'POST',
        contentType: p.contentType,
        processData,
        dataType: p.dataType || 'json',
        async,
        beforeSend(xhr) {
            xhr.setRequestHeader('prgmId', pgmId);
            xhr.setRequestHeader('menuId', menuId);
        },
        success(data) {
            if (async) {
                hideLoading();
            }
            if (_.has(p, 'success') && _.isFunction(p.success)) {
                p.success.call(this, data);
            }
        },
        error(xhr, status, error) {
            if (async) {
                hideLoading();
            }
            switch (xhr.status) {
            case 0:
                return;
            case 401:
                openDialog('message', '권한이 없습니다.');
                break;
            case 403:
                top.location.href = '/sys/error/403.jsp';
                break;
            case 404:
                openDialog('message', '유효한 요청이 아닙니다.');
                break;
            default: {
                const resText = JSON.parse(xhr.responseText);
                openDialog('message', resText.rtnMap.rtnMsg);
            }
            }

            if (_.has(p, 'error') && _.isFunction(p.error)) {
                p.error.call(this, xhr, status, error);
            }
        },
    });
}

function ajax(p) {
    call(p);
}

function ajaxBulk(p) {
    const rawParam = p;
    let parameters;
    let processData = true;
    let async = true;

    if (_.isEmpty(rawParam.contentType) && !_.isBoolean(rawParam.contentType)) {
        let rowsz = 0;
        if (!rawParam.rowsize) {
            rowsz = 3000;
        } else {
            rowsz = rawParam.rowsize;
        }
        if (!rawParam.data.PAGING) {
            rawParam.data.PAGING = {
                startPage: 1,
                rowSize: rowsz,
            };
            window.tmpBulkDs = [];
            window.tmpBulkDsOthers = {};
            showLoading();
        }
        parameters = { _dataset_: JSON.stringify(rawParam.data) };
        if (rawParam.tempContinusCnt != null) {
            window.tempContinusCnt = rawParam.tempContinusCnt;
        }
    } else if (_.isEqual(rawParam.contentType, 'multipart/form-data') || _.isEqual(rawParam.contentType, false)) {
        parameters = rawParam.data;
        processData = false;
    }

    if (_.isBoolean(rawParam.async)) {
        async = rawParam.async;
    }

    let pgmId;
    let menuId;
    try {
        pgmId = window[top.tabContainer.getActiveTabIndex() + 1].GFN_CONTEXT_INFO.pgmId;
        menuId = window[top.tabContainer.getActiveTabIndex() + 1].GFN_CONTEXT_INFO.menuId;
    } catch (error) {
        pgmId = '';
        menuId = '';
    }

    $.ajax({
        url: xss(rawParam.url),
        data: parameters,
        method: rawParam.method || 'POST',
        contentType: rawParam.contentType,
        processData,
        dataType: rawParam.dataType || 'json',
        async,
        beforeSend(xhr) {
            xhr.setRequestHeader('prgmId', pgmId);
            xhr.setRequestHeader('menuId', menuId);
        },
        success(data) {
            const regex = /(.*)_paging/;
            let strDsName;
            Object.keys(data).forEach((key) => {
                if (regex.exec(key) != null) {
                    strDsName = regex.exec(key)[1];
                }
            });
            Object.keys(data).forEach((key) => {
                if (!(key === strDsName || key === `${strDsName}_paging`)) {
                    window.tmpBulkDsOthers[key] = data[key];
                }
            });
            if (strDsName == null || data[strDsName] == null || data[strDsName].length === 0) {
                hideLoading();
                rawParam.success.call(this, data);
                return false;
            }
            window.tmpBulkDs = $.merge(window.tmpBulkDs, data[strDsName]);
            const currpage = data[`${strDsName}_paging`].currentPage;
            const totcnt = data[`${strDsName}_paging`].rows;
            const rowsz = data[`${strDsName}_paging`].rowSize;
            let totpage = _.ceil(totcnt / rowsz);
            totpage = _.isNaN(totpage) ? 0 : totpage;
            $('#LAYER_LOADING > p').text(`loading.. (${currpage}/${totpage}`);
            if ($('#LAYER_LOADING').css('display') !== 'block') {
                return false;
            }
            if (currpage >= totpage || currpage === window.tempContinusCnt) {
                const map = {};
                map[strDsName] = window.tmpBulkDs;
                Object.keys(window.tmpBulkDsOthers).forEach((key) => {
                    map[key] = window.tmpBulkDsOthers[key];
                });
                if (_.isFunction(rawParam.success)) {
                    hideLoading();
                    $('#LAYER_LOADING > p').text('loading..');
                    rawParam.success.call(this, map);
                }
            } else {
                rawParam.data.PAGING = {
                    rowSize: rowsz,
                    startPage: currpage + 1,
                };
                ajaxBulk(rawParam);
            }
            return null;
        },
        error(xhr, status, error) {
            hideLoading();
            switch (xhr.status) {
            case 0:
                return;
            case 401:
                openDialog('message', '권한이 없습니다.');
                break;
            case 403:
                top.location.href = '/sys/error/403.jsp';
                break;
            case 404:
                openDialog('message', '유효한 요청이 아닙니다.');
                break;
            default: {
                const resText = JSON.parse(xhr.responseText);
                if (!_.isEmpty(resText.rtnMap) && !_.isEmpty(resText.rtnMap.rtnMsg)) {
                    openDialog('message', resText.rtnMap.rtnMsg);
                }
            }
            }

            if (_.isFunction(p.error)) {
                p.error.call(this, xhr, status, error);
            }
        },
        complete() {
        },
    });
}

function ajaxMore(p) {
    const rawParam = p;
    let parameters;
    const processData = true;
    let async = false;

    if (_.isEmpty(rawParam.contentType) && !_.isBoolean(rawParam.contentType)) {
        let startpg = 0;
        let rowsz = 0;
        if (!rawParam.rowsize) {
            rowsz = 5;
        } else {
            rowsz = rawParam.rowsize;
        }
        if (!window.tmpPaging) {
            startpg = 1;
        } else {
            startpg = _.toInteger(window.tmpPaging.currentPage) + 1;
        }
        rawParam.data.PAGING = {
            rowSize: rowsz,
            startPage: startpg,
        };
        showLoading();
        parameters = { _dataset_: JSON.stringify(p.data) };
    }

    if (_.isBoolean(rawParam.async)) {
        async = rawParam.async;
    }

    let pgmId;
    let menuId;
    try {
        pgmId = window[top.tabContainer.getActiveTabIndex() + 1].GFN_CONTEXT_INFO.pgmId;
        menuId = window[top.tabContainer.getActiveTabIndex() + 1].GFN_CONTEXT_INFO.menuId;
    } catch (error) {
        pgmId = '';
        menuId = '';
    }

    $.ajax({
        url: xss(rawParam.url),
        data: parameters,
        method: rawParam.method || 'POST',
        contentType: rawParam.contentType,
        processData,
        dataType: rawParam.dataType || 'json',
        async,
        beforeSend(xhr) {
            xhr.setRequestHeader('prgmId', pgmId);
            xhr.setRequestHeader('menuId', menuId);
        },
        success(data) {
            const regex = /(.*)_paging/;
            let strDsName;
            Object.keys(data).forEach((key) => {
                if (regex.exec(key) != null) {
                    strDsName = regex.exec(key)[1];
                }
            });
            window.tmpPaging = data[`${strDsName}_paging`];
            hideLoading();
            rawParam.success.call(this, data);
            return null;
        },
        error(xhr, status, error) {
            hideLoading();
            switch (xhr.status) {
            case 0:
                return;
            case 401:
                openDialog('message', '권한이 없습니다.');
                break;
            case 403:
                top.location.href = '/sys/error/403.jsp';
                break;
            case 404:
                openDialog('message', '유효한 요청이 아닙니다.');
                break;
            default: {
                const resText = JSON.parse(xhr.responseText);
                if (!_.isEmpty(resText.rtnMap) && !_.isEmpty(resText.rtnMap.rtnMsg)) {
                    openDialog('message', resText.rtnMap.rtnMsg);
                }
            }
            }

            if (_.isFunction(p.error)) {
                p.error.call(this, xhr, status, error);
            }
        },
        complete() {
            hideLoading();
        },
    });
}


function ajaxPaging(p) {
    const rawParam = p;
    let parameters;
    const processData = true;
    let async = false;

    if (!rawParam.grid) {
        throw new Error('ajaxPaging is required grid-object.');
    } else {
        rawParam.data.PAGING = rawParam.grid.PAGING;
    }


    if (_.isEmpty(rawParam.contentType) && !_.isBoolean(rawParam.contentType)) {
        parameters = { _dataset_: JSON.stringify(p.data) };
    }

    if (_.isBoolean(rawParam.async)) {
        async = rawParam.async;
    }
    if (rawParam.showLoading !== false) {
        showLoading();
    }

    let pgmId;
    let menuId;
    try {
        pgmId = window[top.tabContainer.getActiveTabIndex() + 1].GFN_CONTEXT_INFO.pgmId;
        menuId = window[top.tabContainer.getActiveTabIndex() + 1].GFN_CONTEXT_INFO.menuId;
    } catch (error) {
        pgmId = '';
        menuId = '';
    }

    $.ajax({
        url: xss(rawParam.url),
        data: parameters,
        method: rawParam.method || 'POST',
        contentType: rawParam.contentType,
        processData,
        dataType: rawParam.dataType || 'json',
        async,
        beforeSend(xhr) {
            xhr.setRequestHeader('prgmId', pgmId);
            xhr.setRequestHeader('menuId', menuId);
        },
        success(data) {
            hideLoading();
            const regex = /(.*)_paging/;
            let strDsName;
            Object.keys(data).forEach((key) => {
                if (regex.exec(key) != null) {
                    strDsName = regex.exec(key)[1];
                }
            });
            rawParam.success.call(this, data);
            rawParam.grid.setPaging(data, strDsName);
            return null;
        },
        error(xhr, status, error) {
            hideLoading();
            switch (xhr.status) {
            case 0:
                return;
            case 401:
                openDialog('message', '권한이 없습니다.');
                break;
            case 403:
                top.location.href = '/sys/error/403.jsp';
                break;
            case 404:
                openDialog('message', '유효한 요청이 아닙니다.');
                break;
            default: {
                const resText = JSON.parse(xhr.responseText);
                if (!_.isEmpty(resText.rtnMap) && !_.isEmpty(resText.rtnMap.rtnMsg)) {
                    openDialog('message', resText.rtnMap.rtnMsg);
                }
            }
            }

            if (_.isFunction(p.error)) {
                p.error.call(this, xhr, status, error);
            }
        },
        complete() {
            hideLoading();
        },
    });
}

function resetMoreAttr() {
    window.tmpPaging = null;
}

function setMoreAttr(p) {
    window.tmpPaging = $.merge(window.tmpPaging, p);
}

function getMoreAttr() {
    if (!window.tmpPaging) {
        return {};
    }
    return window.tmpPaging;
}

function submit(p) {
    const ajaxData = p;
    ajaxData.contentType = false;
    call(ajaxData);
}

function getCommonCode(p) {
    const ajaxData = p;
    ajaxData.url = '/com/code/selComCdList.dev';

    const inputData = {
        IN_PARAM: p.data,
    };

    ajaxData.success = (data) => {
        if (_.has(p, 'select')) {
            const select = p.select;
            if (_.has(data, 'outDs')) {
                const result = data.outDs;
                const placeholder = _.find(result, r => _.isEmpty(r.code));
                if (!_.isEmpty(placeholder)) {
                    select.setPlaceholder(placeholder.name, true);
                }

                select.setData(result, 'code', 'name');
            }
        }
    };
    ajaxData.data = inputData;

    call(ajaxData);
}

export { ajax, ajaxBulk, ajaxMore, ajaxPaging, resetMoreAttr, setMoreAttr, getMoreAttr, submit, getCommonCode };
