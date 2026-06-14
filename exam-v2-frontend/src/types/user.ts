// 定义用户角色：ADMIN-管理员，TEACHER-教师，STUDENT-学生
export type UserRole = 'ADMIN' | 'TEACHER' | 'STUDENT'

// 登录成功后的用户信息数据结构
export interface LoggedInUser {
  username: string       // 账号/学号/工号
  displayName: string    // 显示的真实姓名
  role: UserRole         // 权限角色
  tableName?: string     // 对应的旧数据库表名（保留旧业务思路）
  userId?: number        // 数据库主键 ID（studentId / teacherId / adminId），用于提交试卷等操作
}