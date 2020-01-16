package pl.zaznaczysz.tool

import pl.zaznaczysz.provider.ActivityProvider
import pl.zaznaczysz.provider.UserProvider

class CommonTool {
    companion object {

        fun updateUserActivity(userId: Int, groupId: Int, points: Int) {
            if (groupId != 0) {
                ActivityProvider.updateActivityUser(
                    userId, groupId, points
                )
            }
            UserProvider.updateActivityUser(
                userId,
                points
            )
        }
    }

}