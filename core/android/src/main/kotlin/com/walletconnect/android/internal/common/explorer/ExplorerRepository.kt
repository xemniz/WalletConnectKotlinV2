package com.walletconnect.android.internal.common.explorer

import androidx.core.net.toUri
import com.walletconnect.android.internal.common.explorer.data.model.App
import com.walletconnect.android.internal.common.explorer.data.model.Colors
import com.walletconnect.android.internal.common.explorer.data.model.DappListings
import com.walletconnect.android.internal.common.explorer.data.model.Desktop
import com.walletconnect.android.internal.common.explorer.data.model.ImageUrl
import com.walletconnect.android.internal.common.explorer.data.model.Injected
import com.walletconnect.android.internal.common.explorer.data.model.Listing
import com.walletconnect.android.internal.common.explorer.data.model.Metadata
import com.walletconnect.android.internal.common.explorer.data.model.Mobile
import com.walletconnect.android.internal.common.explorer.data.model.NotificationType
import com.walletconnect.android.internal.common.explorer.data.model.NotifyConfig
import com.walletconnect.android.internal.common.explorer.data.model.Project
import com.walletconnect.android.internal.common.explorer.data.model.ProjectListing
import com.walletconnect.android.internal.common.explorer.data.model.SupportedStandard
import com.walletconnect.android.internal.common.explorer.data.network.ExplorerService
import com.walletconnect.android.internal.common.explorer.data.network.model.AppDTO
import com.walletconnect.android.internal.common.explorer.data.network.model.ColorsDTO
import com.walletconnect.android.internal.common.explorer.data.network.model.DappListingsDTO
import com.walletconnect.android.internal.common.explorer.data.network.model.DesktopDTO
import com.walletconnect.android.internal.common.explorer.data.network.model.ImageUrlDTO
import com.walletconnect.android.internal.common.explorer.data.network.model.InjectedDTO
import com.walletconnect.android.internal.common.explorer.data.network.model.ListingDTO
import com.walletconnect.android.internal.common.explorer.data.network.model.MetadataDTO
import com.walletconnect.android.internal.common.explorer.data.network.model.MobileDTO
import com.walletconnect.android.internal.common.explorer.data.network.model.NotificationTypeDTO
import com.walletconnect.android.internal.common.explorer.data.network.model.NotifyConfigDTO
import com.walletconnect.android.internal.common.explorer.data.network.model.ProjectDTO
import com.walletconnect.android.internal.common.explorer.data.network.model.ProjectListingDTO
import com.walletconnect.android.internal.common.explorer.data.network.model.SupportedStandardDTO
import com.walletconnect.android.internal.common.model.ProjectId

//discuss: Repository could be inside domain
class ExplorerRepository(
    private val explorerService: ExplorerService,
    private val projectId: ProjectId,
) {

    suspend fun getAllDapps(): DappListings {
        return with(explorerService.getAllDapps(projectId.value)) {
            if (isSuccessful && body() != null) {
                body()!!.toDappListing()
            } else {
                throw Throwable(errorBody()?.string())
            }
        }
    }

    suspend fun getProjects(
        page: Int,
        entries: Int,
        isVerified: Boolean,
    ): ProjectListing {
        return with(explorerService.getProjects(projectId.value, entries, page, isVerified)) {
            if (isSuccessful && body() != null) {
                body()!!.toProjectListing()
            } else {
                throw Throwable(errorBody()?.string())
            }
        }
    }

    suspend fun getNotifyConfig(
        appDomain: String,
    ): NotifyConfig {
        return with(explorerService.getNotifyConfig(projectId.value, appDomain)) {
            if (isSuccessful && body() != null) {
                body()!!.toNotifyConfig()
            } else {
                throw Throwable(errorBody()?.string())
            }
        }
    }

    private fun NotifyConfigDTO.toNotifyConfig(): NotifyConfig {
        return with(data) {
            NotifyConfig(
                types = notificationTypes.map { it.toNotificationType() },
                name = name,
                description = description,
                imageUrl = imageUrl.toImageUrl(),
                homepage = homepage,
                dappUrl = dappUrl,
                isVerified = isVerified,
            )
        }
    }

    private fun NotificationTypeDTO.toNotificationType(): NotificationType = NotificationType(name = name, id = id, description = description)


    private fun ProjectListingDTO.toProjectListing(): ProjectListing {
        return ProjectListing(
            projects = projects.values.map { it.toProject() },
            count = count,
        )
    }

    private fun ProjectDTO.toProject(): Project = Project(
        id = id,
        name = name?.takeIf { it.isNotBlank() } ?: "Name not provided",
        description = description?.takeIf { it.isNotBlank() } ?: "Description not provided",
        homepage = homepage?.takeIf { it.isNotBlank() } ?: "Homepage not provided",
        imageId = imageId?.takeIf { it.isNotBlank() } ?: "ImageID not provided",
        imageUrl = imageUrl?.toImageUrl() ?: ImageUrl("", "", ""),
        dappUrl = dappUrl?.takeIf { it.isNotBlank() } ?: "Dapp url not provided",
    )

    private fun DappListingsDTO.toDappListing(): DappListings {
        return DappListings(
            listings = listings.values.map { it.toListing() }, count = count, total = total
        )
    }

    private fun ListingDTO.toListing(): Listing = Listing(
        id = id,
        name = name,
        description = description,
        homepage = homepage.toUri(),
        chains = chains,
        versions = versions,
        sdks = sdks,
        appType = appType,
        imageId = imageId,
        imageUrl = imageUrl.toImageUrl(),
        app = app.toApp(),
        injected = injected?.map { it.toInjected() },
        mobile = mobile.toMobile(),
        desktop = desktop.toDesktop(),
        supportedStandards = supportedStandards.map { it.toSupportedStandard() },
        metadata = metadata.toMetadata(),
        updatedAt = updatedAt
    )

    private fun ImageUrlDTO.toImageUrl(): ImageUrl = ImageUrl(
        sm = sm,
        md = md,
        lg = lg,
    )

    private fun AppDTO.toApp(): App = App(
        browser = browser, ios = ios, android = android, mac = mac, windows = windows, linux = linux, chrome = chrome, firefox = firefox, safari = safari, edge = edge, opera = opera
    )

    private fun InjectedDTO.toInjected(): Injected = Injected(
        namespace = namespace, injectedId = injectedId
    )

    private fun MobileDTO.toMobile(): Mobile = Mobile(
        native = native,
        universal = universal,
    )

    private fun DesktopDTO.toDesktop(): Desktop = Desktop(
        native = native,
        universal = universal,
    )

    private fun SupportedStandardDTO.toSupportedStandard(): SupportedStandard = SupportedStandard(
        id = id, url = url, title = title, standardId = standardId, standardPrefix = standardPrefix
    )

    private fun MetadataDTO.toMetadata(): Metadata = Metadata(
        shortName = shortName,
        colors = colors.toColors(),
    )

    private fun ColorsDTO.toColors(): Colors = Colors(
        primary = primary, secondary = secondary
    )
}