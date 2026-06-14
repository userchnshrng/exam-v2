export type UserRole = 'ADMIN' | 'TEACHER' | 'STUDENT'

export interface LoggedInUser {
  username: string
  displayName: string
  role: UserRole
  tableName?: string
}
