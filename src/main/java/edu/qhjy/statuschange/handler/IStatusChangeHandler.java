package edu.qhjy.statuschange.handler;

/**
 * 学籍异动善后处理器通用接口
 */
public interface IStatusChangeHandler {

    /**
     * 获取本处理器能处理的异动类型ID (KJYDLXBS)
     *
     * @return 异动类型ID
     */
    Long getActionType();

    /**
     * 执行具体的学籍信息更新操作
     *
     * @param kjydjlbs 考籍异动记录的ID
     */
    void apply(Long kjydjlbs);
}