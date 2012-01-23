The HttpChannel library is a library that povides downloading and uploading capabilities to several share-sites (such as MegaUpload and FileSonic).
Obviously, the API supports a lot more services, but those are the most commonly used. Aside from that, the biggest point of the library is its simple usage, you don't need to use any customized API to perform download or uploads, you simply use the standard Java NIO Channels for both upload and download.

	final UploadService<?> service = Services.getUploadService("megaupload");
	final Uploader<?> uploader = UploadServices.upload(service, Path.get("test-file.txt"));
	final UploadChannel channel = uploader.openChannel();
	// now, you can perform any operation you want with this channel! Lets copy its data
	ChannelUtils.copy(inputChannel, channel);
	// this may take some time, it will finish the upload and generate the download link
	channel.close();
	System.out.println("Download Link: "+channel.getDownloadLink());