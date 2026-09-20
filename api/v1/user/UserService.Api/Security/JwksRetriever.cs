// JwksRetriever.cs
using Microsoft.IdentityModel.Protocols;
using Microsoft.IdentityModel.Tokens;

namespace UserService.Api.Security;

internal sealed class JwksRetriever : IConfigurationRetriever<JsonWebKeySet>
{
    public async Task<JsonWebKeySet> GetConfigurationAsync(
        string address, IDocumentRetriever retriever, CancellationToken cancel)
        => new(await retriever.GetDocumentAsync(address, cancel));
}